package vf.arbiter.command.controller

import utopia.bunnymunch.jawn.JsonBunny
import utopia.citadel.database.access.many.description.{DbDescriptionRoles, DbLanguageDescriptions, DbLanguageFamiliarityDescriptions, LinkedDescriptionsAccess}
import utopia.citadel.database.access.many.language.DbLanguages
import utopia.citadel.database.access.many.user.DbManyUserSettings
import utopia.citadel.database.model.language.LanguageModel
import utopia.citadel.database.model.user.{UserModel, UserSettingsModel}
import utopia.flow.datastructure.immutable.{Model, ModelDeclaration}
import utopia.flow.generic.{IntType, ModelType, StringType}
import utopia.flow.generic.ValueUnwraps._
import utopia.flow.time.Now
import utopia.flow.util.CollectionExtensions._
import utopia.metropolis.model.partial.description.DescriptionData
import utopia.metropolis.model.partial.language.LanguageData
import utopia.metropolis.model.partial.user.{UserData, UserSettingsData}
import utopia.metropolis.model.stored.description.DescriptionRole
import utopia.vault.database.Connection
import vf.arbiter.core.database.access.many.company.{DbBanks, DbCompanies, DbDetailedCompanies, DbManyCompanyDetails}
import vf.arbiter.core.database.access.many.description.DbItemUnitDescriptions
import vf.arbiter.core.database.access.many.location.{DbCounties, DbPostalCodes}
import vf.arbiter.core.database.access.single.location.DbStreetAddress
import vf.arbiter.core.database.model.company.{BankModel, CompanyDetailsModel, CompanyModel}
import vf.arbiter.core.database.model.location.{CountyModel, PostalCodeModel}
import vf.arbiter.core.model.combined.company.DetailedCompany
import vf.arbiter.core.model.partial.company.{BankData, CompanyData, CompanyDetailsData}
import vf.arbiter.core.model.partial.location.{CountyData, PostalCodeData, StreetAddressData}
import vf.arbiter.core.model.stored.location.PostalCode

import java.nio.file.Path

/**
 * Used for importing previously exported data
 * @author Mikko Hilpinen
 * @since 13.11.2021, v1.2
 */
object ImportData
{
	private val idSchema = ModelDeclaration("id" -> IntType)
	private val descriptionModelSchema = ModelDeclaration("language" -> StringType)
	private val languageSchema = ModelDeclaration("code" -> StringType)
	private val userSchema = ModelDeclaration("id" -> IntType, "name" -> StringType)
	private val bankSchema = ModelDeclaration("bic" -> StringType, "name" -> StringType)
	private val companySchema = ModelDeclaration("y_code" -> StringType)
	private val companyDetailsSchema = idSchema.withChild("address", ModelDeclaration(
		"street_name" -> StringType, "building_number" -> StringType)
		.withChild("postal_code", ModelDeclaration("county" -> StringType, "code" -> StringType)))
	
	def fromJson(path: Path)(implicit connection: Connection) = JsonBunny.munchPath(path).map { root =>
		val descriptionRoles = DbDescriptionRoles.pull
		// Imports languages and language familiarities
		val (descriptionContext, languageFailures) = importLanguages(root("languages").getVector.flatMap { _.model },
			descriptionRoles)
		implicit val dContext: DescriptionContext = descriptionContext
		val familiarityFailures = importLanguageFamiliarities(
			root("language_familiarities").getVector.flatMap { _.model })
		// Imports users. Remembers which user was linked with which id
		// UserIdMap is { old user id: new user id }
		val (userIdMap, userFailures) = importUsers(root("users").getVector.flatMap { _.model })
		// Imports item units
		val unitFailures = importUnits(root("units").getVector.flatMap { _.model })
		// Imports banks
		val (bankIdPerBic, bankFailures) = importBanks(root("banks").getVector.flatMap { _.model })
		
		// TODO: Import other data
	}
	
	private def importLanguages(languageModels: Vector[Model], descriptionRoles: Iterable[DescriptionRole])
	                           (implicit connection: Connection) = {
		// Validates proposed models
		val (failures, validModels) = languageModels.map { languageSchema.validate(_).toTry }.divided
		val descriptionModelsPerCode = validModels
			.map { model => model("code").getString -> model("descriptions").getVector.flatMap { _.model } }.toMap
		// Reads existing languages
		val existingIdPerCode = DbLanguages.pull.map { lang => lang.isoCode -> lang.id }.toMap
		// Divides the submitted data to new and existing cases
		val (newCases, existingCases) = descriptionModelsPerCode
			.divideBy { case (code, _) => existingIdPerCode.contains(code) }
		// Imports new languages, if there are any
		val newLanguages = LanguageModel.insert((descriptionModelsPerCode.keySet -- existingIdPerCode.keySet).toVector
			.map { LanguageData(_) })
		// Imports new language descriptions
		val languageIdPerCode = existingIdPerCode ++ newLanguages.map { lang => lang.isoCode -> lang.id }
		implicit val descContext: DescriptionContext = DescriptionContext(languageIdPerCode, descriptionRoles.toVector)
		val firstDescriptionFailures = importDescriptions(DbLanguageDescriptions,
			newCases.map { case (code, descriptions) => languageIdPerCode(code) -> descriptions },
			checkForDuplicates = false)
		val secondDescriptionFailures = importDescriptions(DbLanguageDescriptions,
			existingCases.map { case (code, descriptions) => languageIdPerCode(code) -> descriptions })
		
		// Returns description context + possible failures
		descContext -> (failures ++ firstDescriptionFailures ++ secondDescriptionFailures)
	}
	
	def importLanguageFamiliarities(familiarityModels: Vector[Model])
	                               (implicit connection: Connection, context: DescriptionContext) =
	{
		// Simply adds descriptions to existing language familiarities
		// (doesn't expect there to be new familiarity levels)
		importDescriptionsFromModels(DbLanguageFamiliarityDescriptions, familiarityModels)
	}
	
	private def importUsers(userModels: Vector[Model])(implicit connection: Connection) = {
		// Validates proposed models
		val (failures, validModels) = userModels.map { userSchema.validate(_).toTry }.divided
		if (validModels.isEmpty)
			Map[Int, Int]() -> failures
		else {
			val idPerName = validModels.map { model => model("name").getString -> model("id").getInt }.toMap
			// Checks which of the users already exist (based on their name)
			val existingSettings = DbManyUserSettings.withAnyOfNames(idPerName.keys).pull
			val existingNames = existingSettings.map { _.name }.toSet
			val newIdPerName = idPerName.filterNot { case (name, _) => existingNames.contains(name) }
			// Inserts the new users and their settings
			val newUsers = UserModel.insert(Vector.fill(newIdPerName.size)(UserData()))
			val newUsersWithData = newUsers.zip(newIdPerName)
			UserSettingsModel
				.insert(newUsersWithData.map { case (user, (name, _)) => UserSettingsData(user.id, name) })
			// Returns the new user data as an old id -> new id map
			val newUserIdMap = newUsersWithData.map { case (user, (_, oldId)) => oldId -> user.id }.toMap
			// TODO: There might be some problems with case-sensitivity here
			val existingUserIdMap = existingSettings
				.map { settings => idPerName(settings.name) -> settings.userId }.toMap
			(existingUserIdMap ++ newUserIdMap) -> failures
		}
	}
	
	private def importUnits(unitModels: Vector[Model])
	                       (implicit connection: Connection, context: DescriptionContext) =
	{
		// Only imports unit descriptions at this time
		importDescriptionsFromModels(DbItemUnitDescriptions, unitModels)
	}
	
	private def importBanks(bankModels: Vector[Model])(implicit connection: Connection) = {
		// Banks are identified using their BIC
		val (failures, validModels) = bankModels.map { bankSchema.validate(_).toTry }.divided
		val nameForBic = validModels.map { model => model("bic").getString -> model("name").getString }.toMap
		// Reads existing bank data (name for BIC)
		val existingIdForBic = DbBanks.pull.map { bank => bank.bic -> bank.id }.toMap
		// Inserts new banks to the DB
		val newNameForBic = nameForBic -- existingIdForBic.keySet
		val newBanks = BankModel.insert(newNameForBic.toVector.map { case (bic, name) => BankData(name, bic) })
		// Returns bank id for BIC -map + possible failures
		existingIdForBic ++ newBanks.map { bank => bank.bic -> bank.id } -> failures
	}
	
	private def importCompanies(companyModels: Vector[Model], bankIdPerBid: Map[String, Int])
	                           (implicit connection: Connection, context: DescriptionContext) =
	{
		val (failures, validModels) = companyModels.map { companySchema.validate(_).toTry }.divided
		val modelPerYCode = validModels.map { model => model("y_code").getString -> model }.toMap
		// Checks for existing companies (based on y-code)
		val existingCompanyPerYCode = DbDetailedCompanies.withAnyOfYCodes(modelPerYCode.keys).pull
			.map { company => company.yCode -> company }.toMap
		val (updates, newCompanies) = modelPerYCode.divideWith { case (yCode, model) =>
			existingCompanyPerYCode.get(yCode) match {
				case Some(existingCompany) => Left(existingCompany -> model)
				case None => Right(yCode -> model)
			}
		}
		// Inserts new companies
		val newCompanyIdPerYCode = CompanyModel.insert(newCompanies.map { case (yCode, _) => CompanyData(yCode) })
			.map { company => company.yCode -> company.id }.toMap
		val companyIdPerYCode = newCompanyIdPerYCode ++ existingCompanyPerYCode.view.mapValues { _.id }
		// Handles company details next
		val (newDetailsIdPerOldId, detailFailures) = importCompanyDetails(modelPerYCode, updates.map { _._1 },
			newCompanyIdPerYCode.keys, companyIdPerYCode)
		// TODO: Import products and bank accounts
	}
	
	private def importCompanyDetails(companyModelPerYCode: Map[String, Model],
	                                 existingVersions: Iterable[DetailedCompany], newCompanyYCodes: Iterable[String],
	                                 companyIdPerYCode: Map[String, Int])
	                                (implicit connection: Connection) =
	{
		val (detailParseFailures, detailModels) = companyModelPerYCode.splitFlatMap { case (yCode, companyModel) =>
			val (detailFailures, detailModels) = companyModel("details").getVector.flatMap { _.model }
				.map { companyDetailsSchema.validate(_).toTry }.divided
			detailFailures -> detailModels.map { yCode -> _ }
		}
		val detailsPerYCode = detailModels.asMultiMap
		// Inserts missing postal codes
		val postalCodeIdMap = importPostalCodes(detailModels.map { _._2("address")("postal").getModel })
		// Handles company details next
		def _detailsFromModel(companyId: Int, details: Model) = {
			val oldId = details("id").getInt
			val addressModel = details("address").getModel
			val postalCodeModel = addressModel("postal_code").getModel
			val address = DbStreetAddress.getOrInsert(StreetAddressData(
				postalCodeIdMap(postalCodeModel("county"))(postalCodeModel("code")),
				addressModel("street_name"), addressModel("building_number"), addressModel("stair"),
				addressModel("room_number")))
			val data = CompanyDetailsData(companyId, details("name"), address.id, details("tax_code"),
				created = details("created"), deprecatedAfter = details("deprecated_after"),
				isOfficial = details("is_official"))
			oldId -> data
		}
		// Updates are in 3 parts:
		// 1) Option[Existing detail id to deprecate]
		// 2) Duplicates in form of [old detail id -> new detail id]
		// 3) [old detail id -> Detail data to insert]
		val detailsUpdates = existingVersions.map { existingCompany =>
			val detailsData = detailsPerYCode(existingCompany.yCode).map { _detailsFromModel(existingCompany.id, _) }
			// Checks for possible duplicate data
			val (duplicateIdConversion, newData) = detailsData.divideWith { case (oldDetailsId, data) =>
				if (data ~== existingCompany.details.data)
					Left(oldDetailsId -> existingCompany.details.id)
				else
					Right(oldDetailsId -> data)
			}
			// Deprecates the imported details, unless they are official and the existing company details aren't
			def _deprecate(data: (Int, CompanyDetailsData)) = {
				if (data._2.isValid)
					data._1 -> data._2.copy(deprecatedAfter = Some(Now))
				else
					data
			}
			if (existingCompany.details.isOfficial)
				(None, duplicateIdConversion, newData.map(_deprecate))
			else
				newData.find { case (_, data) => data.isValid && data.isOfficial } match {
					case Some((oldDetailsId, newMainDetails)) =>
						(Some(existingCompany.details.id), duplicateIdConversion,
							(oldDetailsId -> newMainDetails) +:
								newData.filterNot { _._1 == oldDetailsId }.map(_deprecate))
					case None => (None, duplicateIdConversion, newData.map(_deprecate))
				}
		}
		// Deprecates some existing company details
		val detailIdsToDeprecate = detailsUpdates.flatMap { _._1 }.toSet
		if (detailIdsToDeprecate.nonEmpty)
			DbManyCompanyDetails(detailIdsToDeprecate).deprecate()
		// Inserts new details
		val newDetailsData = newCompanyYCodes.flatMap { yCode =>
			val companyId = companyIdPerYCode(yCode)
			val detailModels = detailsPerYCode(yCode)
			detailModels.map { _detailsFromModel(companyId, _) }
		}
		val allDetailsDataToInsert = (newDetailsData ++ detailsUpdates.flatMap { _._3 }).toVector
		val insertedDetails = CompanyDetailsModel.insert(allDetailsDataToInsert.map { _._2 })
		val newDetailIdPerOldId = insertedDetails.zip(allDetailsDataToInsert)
			.map { case (inserted, (oldId, _)) => oldId -> inserted.id }.toMap ++ detailsUpdates.flatMap { _._2 }
		
		// Returns new detail id per old detail id -map + possible parsing failures
		newDetailIdPerOldId -> detailParseFailures
	}
	
	// Returns: [CountyName: [PostalCode: PostalCodeId]]
	private def importPostalCodes(postalModels: Iterable[Model])(implicit connection: Connection) =
	{
		// Inserts new counties
		val countyNames = postalModels.map { _("county").getString }.toSet
		val existingCounties = DbCounties.withAnyOfNames(countyNames).pull
		val existingCountyIdPerName = existingCounties.map { c => c.name -> c.id }.toMap
		val newCounties = CountyModel.insert(countyNames.filterNot(existingCountyIdPerName.contains).toVector
			.map { CountyData(_) })
		val newCountyIdPerName = newCounties.map{ c => c.name -> c.id }.toMap
		val countyNamePerId = (existingCounties ++ newCounties).map { c => c.id -> c.name }.toMap
		
		// Inserts new postal codes
		val (possibleDuplicatePostal, certainlyNewPostal) = postalModels.divideWith { postalModel =>
			val countyName = postalModel("county").getString
			val code = postalModel("code").getString
			existingCountyIdPerName.get(countyName) match {
				case Some(oldCountyId) => Left(oldCountyId -> code)
				// TODO: Again, possibility for case-error
				case None => Right(newCountyIdPerName(countyName) -> code)
			}
		}
		
		def _postalsToMap(postals: Iterable[PostalCode]) = postals.groupBy { _.countyId }
			.map { case (countyId, codes) => countyNamePerId(countyId) -> codes.map { c => c.number -> c.id }.toMap }
		
		if (possibleDuplicatePostal.nonEmpty) {
			val existingPostals = DbPostalCodes.inCountiesWithIds(possibleDuplicatePostal.map { _._1 }.toSet)
				.withAnyOfCodes(possibleDuplicatePostal.map { _._2 }.toSet).pull
			val definedCodesPerCountyId = existingPostals.map { postal => postal.countyId -> postal.number }.asMultiMap
			val newPostals = possibleDuplicatePostal.filterNot { case (countyId, code) =>
				definedCodesPerCountyId.get(countyId).exists { _.contains(code) } } ++ certainlyNewPostal
			val insertedPostals = PostalCodeModel.insert(
				newPostals.map { case (countyId, code) => PostalCodeData(code, countyId) })
			_postalsToMap(existingPostals ++ insertedPostals)
		}
		else {
			val insertedPostals = PostalCodeModel.insert(certainlyNewPostal
				.map { case (countyId, number) => PostalCodeData(number, countyId) })
			_postalsToMap(insertedPostals)
		}
	}
	
	private def importDescriptionsFromModels(descriptionsAccess: LinkedDescriptionsAccess,
	                                         describedModels: Iterable[Model], checkForDuplicates: Boolean = true)
	                                        (implicit connection: Connection, context: DescriptionContext) =
	{
		// Processes the models
		val (failures, validModels) = describedModels.map { idSchema.validate(_).toTry }.divided
		val descriptionModels = validModels.flatMap { model =>
			val id = model("id").getInt
			model("descriptions").getVector.flatMap { _.model }.map { id -> _ }
		}.asMultiMap
		val descriptionImportFailures = importDescriptions(descriptionsAccess, descriptionModels, checkForDuplicates)
		// Returns encountered failures
		failures ++ descriptionImportFailures
	}
	
	private def importDescriptions(descriptionsAccess: LinkedDescriptionsAccess,
	                               descriptionModelsPerId: Map[Int, Vector[Model]],
	                               checkForDuplicates: Boolean = true)
	                              (implicit connection: Connection, context: DescriptionContext) =
	{
		if (descriptionModelsPerId.nonEmpty) {
			// Parses specified description models into description data. Collects possible failures, also.
			val (failures, proposedDescriptions) = descriptionModelsPerId.view.mapValues { models =>
				val (failures, valid) = models.map { descriptionModelSchema.validate(_).toTry }.divided
				val (languageFailures, descriptions) = valid.flatDivideWith { model => {
					// The specified language code must be valid (map to an id)
					val languageCode = model("language").getString
					context.languageIdPerCode.get(languageCode) match {
						case Some(languageId) =>
							val descriptionsModel = model("descriptions").getModel
							// Looks for descriptions for each description role (singular and plural versions)
							context.descriptionRoles.flatMap { role =>
								val singularDescription = descriptionsModel(role.jsonKeySingular).string
								val pluralDescriptions = descriptionsModel(role.jsonKeyPlural)
									.getVector.flatMap { _.string }
								(pluralDescriptions ++ singularDescription)
									.map { text => Right(DescriptionData(role.id, languageId, text)) }
							}
						case None => Some(Left(new NoSuchElementException(s"No language for code '$languageCode'")))
					}
				} }
				(failures ++ languageFailures) -> descriptions
			}.splitFlatMap { case (targetId, (failures, descriptions)) =>
				failures -> descriptions.map { d => targetId -> d }
			}
			
			// Case: Possible duplicates / overwrites need to be avoided
			if (checkForDuplicates) {
				// Reads existing descriptions concerning these items
				val proposedDescriptionsPerTargetId = proposedDescriptions.groupMap { _._1 } { _._2 }
				val usedLanguageIds = proposedDescriptions.map { _._2.languageId }.toSet
				val usedRoleIds = proposedDescriptions.map { _._2.roleId }.toSet
				val existingDescriptionsPerTargetId = descriptionsAccess(proposedDescriptionsPerTargetId.keySet)
					.inLanguagesWithIds(usedLanguageIds).withRoleIds(usedRoleIds).pull.groupBy { _.targetId }
				// Removes duplicates
				val newDescriptions = proposedDescriptionsPerTargetId.toVector
					.flatMap { case (targetId, descriptions) =>
						val existingDescriptions = existingDescriptionsPerTargetId.getOrElse(targetId, Vector())
						val existingRoleIdsPerLanguageId = existingDescriptions.groupMap { _.languageId } { _.roleId }
							.view.mapValues { _.toSet }.toMap
						descriptions.filterNot { proposed => existingRoleIdsPerLanguageId.get(proposed.languageId)
							.exists { _.contains(proposed.roleId) } }.map { targetId -> _ }
					}
				descriptionsAccess.linkModel.insertDescriptions(newDescriptions)
			}
			// Case: Duplicates & overwrites don't need to be worried about => just inserts all data in a single dump
			else
				descriptionsAccess.linkModel.insertDescriptions(proposedDescriptions)
			
			// Returns encountered failures
			failures
		}
		else
			Vector()
	}
	
	
	// NESTED   ----------------------------------
	
	private case class DescriptionContext(languageIdPerCode: Map[String, Int],
	                                      descriptionRoles: Vector[DescriptionRole])
}
