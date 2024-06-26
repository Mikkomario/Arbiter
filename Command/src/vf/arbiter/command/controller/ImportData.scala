package vf.arbiter.command.controller

import utopia.bunnymunch.jawn.JsonBunny
import utopia.citadel.database.access.many.description.{DbDescriptionRoles, DbLanguageDescriptions, DbLanguageFamiliarityDescriptions, DbOrganizationDescriptions, LinkedDescriptionsAccess}
import utopia.citadel.database.access.many.language.DbLanguages
import utopia.citadel.database.access.many.organization.{DbMemberships, DbUserRoles}
import utopia.citadel.database.access.many.user.DbManyUserSettings
import utopia.citadel.database.model.language.LanguageModel
import utopia.citadel.database.model.organization.{MemberRoleLinkModel, MembershipModel, OrganizationModel}
import utopia.citadel.database.model.user.{UserModel, UserSettingsModel}
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration}
import utopia.flow.generic.model.mutable.DataType.{DoubleType, InstantType, IntType, StringType}
import utopia.flow.generic.casting.ValueUnwraps._
import utopia.flow.time.{DateRange, Days, Now}
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Pair
import utopia.metropolis.model.partial.description.DescriptionData
import utopia.metropolis.model.partial.language.LanguageData
import utopia.metropolis.model.partial.organization.{MemberRoleLinkData, MembershipData, OrganizationData}
import utopia.metropolis.model.partial.user.{UserData, UserSettingsData}
import utopia.metropolis.model.stored.description.DescriptionRole
import utopia.vault.database.Connection
import vf.arbiter.core.database.access.many.company.{DbBanks, DbCompanyBankAccounts, DbDetailedCompanies, DbManyCompanyDetails, DbOrganizationCompanies}
import vf.arbiter.core.database.access.many.description.{DbCompanyProductDescriptions, DbItemUnitDescriptions}
import vf.arbiter.core.database.access.many.location.{DbCounties, DbPostalCodes}
import vf.arbiter.core.database.access.single.location.DbStreetAddress
import vf.arbiter.core.database.model.company.{BankModel, CompanyBankAccountModel, CompanyDetailsModel, CompanyModel, CompanyProductModel, OrganizationCompanyModel}
import vf.arbiter.core.database.model.invoice.{InvoiceItemModel, InvoiceModel}
import vf.arbiter.core.database.model.location.{CountyModel, PostalCodeModel}
import vf.arbiter.core.model.combined.company.DetailedCompany
import vf.arbiter.core.model.partial.company.{BankData, CompanyBankAccountData, CompanyData, CompanyDetailsData, CompanyProductData, OrganizationCompanyData}
import vf.arbiter.core.model.partial.invoice.{InvoiceData, InvoiceItemData}
import vf.arbiter.core.model.partial.location.{CountyData, PostalCodeData, StreetAddressData}
import vf.arbiter.core.model.stored.location.PostalCode

import java.nio.file.Path
import scala.util.{Failure, Success}

/**
 * Used for importing previously exported data
 * @author Mikko Hilpinen
 * @since 13.11.2021, v1.2
 */
// FIXME: When importing invoices, the invoice ids are swapped (from low to high)
object ImportData
{
	private lazy val idSchema = ModelDeclaration("id" -> IntType)
	private lazy val descriptionModelSchema = ModelDeclaration("language" -> StringType)
	private lazy val languageSchema = ModelDeclaration("code" -> StringType)
	private lazy val userSchema = ModelDeclaration("id" -> IntType, "name" -> StringType)
	private lazy val bankSchema = ModelDeclaration("bic" -> StringType, "name" -> StringType)
	private lazy val companySchema = ModelDeclaration("y_code" -> StringType)
	private lazy val companyDetailsSchema = idSchema.withChild("address", ModelDeclaration(
		"street_name" -> StringType, "building_number" -> StringType)
		.withChild("postal_code", ModelDeclaration("county" -> StringType, "code" -> StringType)))
	private lazy val productSchema = ModelDeclaration("id" -> IntType, "unit_id" -> IntType)
	private lazy val bankAccountSchema = ModelDeclaration("bic" -> StringType, "iban" -> StringType)
	private lazy val invoiceSchema = ModelDeclaration(
		"id" -> IntType, "reference_code" -> StringType,
		"sender_details_id" -> IntType, "recipient_details_id" -> IntType, "language" -> StringType,
		"created" -> InstantType, "payment_duration_days" -> IntType)
		.withChild("sender_bank_account",
			ModelDeclaration("bic" -> StringType, "iban" -> StringType))
	private lazy val invoiceItemSchema = ModelDeclaration(
		"product_id" -> IntType, "description" -> StringType,
		"price_per_unit" -> DoubleType, "units_sold" -> DoubleType)
	
	/**
	 * Imports all sorts of data (users, companies, languages, invoices) from a json file
	 * @param path Path to an existing data json file
	 * @param connection Implicit DB Connection
	 * @return Whether file read (json parsing) succeeded or failed.
	 *         Content-related failures are documented within this method call.
	 */
	def fromJson(path: Path)(implicit connection: Connection) = JsonBunny.munchPath(path).map { root =>
		val descriptionRoles = DbDescriptionRoles.pull
		//println(s"Using description roles: [${descriptionRoles.map { _.jsonKeySingular }.mkString(", ")}]")
		// Imports languages and language familiarities
		val (descriptionContext, languageFailures) = importLanguages(root("languages").getVector.flatMap { _.model },
			descriptionRoles)
		//println(s"Languages: {${descriptionContext.languageIdPerCode.map { case (code, id) => s"$code: $id" }.mkString(", ") }}")
		implicit val dContext: DescriptionContext = descriptionContext
		val familiarityFailures = importLanguageFamiliarities(
			root("language_familiarities").getVector.flatMap { _.model })
		// Imports users. Remembers which user was linked with which id
		// UserIdMap is { old user id: new user id }
		val (userIdMap, userFailures) = importUsers(root("users").getVector.flatMap { _.model })
		//println(s"User Id Conversion: {${userIdMap.map { case (old, newId) => s"$old => $newId" }.mkString(", ") }}")
		// Imports item units
		val unitFailures = importUnits(root("units").getVector.flatMap { _.model })
		// Imports banks
		val (bankIdPerBic, bankFailures) = importBanks(root("banks").getVector.flatMap { _.model })
		//println(s"Banks: {${bankIdPerBic.map { case (bic, id) => s"$bic: $id" }.mkString(", ") }}")
		// Imports companies and related information
		// companyDetailsIdMap contains [old details id: Pair(new details id, company id)]
		val (companyIdPerYCode, companyDetailsMap, productIdMap, accountsMap, companyFailures) = importCompanies(
			root("companies").getVector.flatMap { _.model }, bankIdPerBic)
		/*
		println(s"Companies: {${companyIdPerYCode.map { case (code, id) => s"$code: $id" }.mkString(", ") }}")
		println(s"Company Details: {${companyDetailsMap
			.map { case (oldId, Pair(newId, companyId)) => s"$oldId: $newId (in $companyId)" }.mkString(", ") }}")
		println(s"Bank Accounts:")
		accountsMap.foreach { case (bankId, accounts) =>
			println(s"- Bank #$bankId (${accounts.size} accounts):")
			accounts.foreach { case (iban, id) => println(s"\t- $iban: $id") }
		}*/
		// Imports organization data
		val organizationFailures = importOrganizations(root("organizations").getVector.flatMap { _.model },
			companyIdPerYCode, userIdMap)
		// Imports invoice data
		val invoiceFailures = importInvoices(root("invoices").getVector.flatMap { _.model }, companyDetailsMap,
			bankIdPerBic, accountsMap, productIdMap)
		
		// Records failures
		val allFailures = languageFailures ++ familiarityFailures ++ userFailures ++ unitFailures ++ bankFailures ++
			companyFailures ++ organizationFailures ++ invoiceFailures
		if (allFailures.nonEmpty) {
			allFailures.head.printStackTrace()
			println(s"Encountered ${allFailures.size} failures during the import:")
			allFailures.foreach { e => println(s"- ${e.getMessage}") }
		}
	}
	
	private def importLanguages(languageModels: Vector[Model], descriptionRoles: Iterable[DescriptionRole])
	                           (implicit connection: Connection) = {
		// Validates proposed models
		val (failures, validModels) = languageModels.map { languageSchema.validate(_) }.divided
		val descriptionModelsPerCode = validModels
			.map { model => model("code").getString -> model("descriptions").getVector.flatMap { _.model } }.toMap
		// Reads existing languages
		val existingIdPerCode = DbLanguages.pull.map { lang => lang.isoCode -> lang.id }.toMap
		// Divides the submitted data to new and existing cases
		val (newCases, existingCases) = descriptionModelsPerCode
			.divideBy { case (code, _) => existingIdPerCode.contains(code) }.toTuple
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
		val (failures, validModels) = userModels.map { userSchema.validate(_) }.divided
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
		val (failures, validModels) = bankModels.map { bankSchema.validate(_) }.divided
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
		val (failures, validModels) = companyModels.map { companySchema.validate(_) }.divided
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
		// Next imports company products
		val (productIdMap, productFailures) = importCompanyProducts(modelPerYCode, companyIdPerYCode)
		// Next imports company bank accounts
		val (accountsMap, accountFailures) = importCompanyBankAccounts(modelPerYCode, companyIdPerYCode, bankIdPerBid)
		
		// Returns collected id-mappers and failures
		(companyIdPerYCode, newDetailsIdPerOldId, productIdMap, accountsMap,
			failures ++ detailFailures ++ productFailures ++ accountFailures)
	}
	
	private def importCompanyDetails(companyModelPerYCode: Map[String, Model],
	                                 existingVersions: Iterable[DetailedCompany], newCompanyYCodes: Iterable[String],
	                                 companyIdPerYCode: Map[String, Int])
	                                (implicit connection: Connection) =
	{
		val (detailParseFailures, detailModels) = companyModelPerYCode.splitFlatMap { case (yCode, companyModel) =>
			val (detailFailures, detailModels) = companyModel("details").getVector.flatMap { _.model }
				.map { companyDetailsSchema.validate(_) }.divided
			detailFailures -> detailModels.map { yCode -> _ }
		}
		val detailsPerYCode = detailModels.asMultiMap
		// Inserts missing postal codes
		val postalCodeIdMap = importPostalCodes(detailModels.map { _._2("address")("postal_code").getModel })
		// Handles company details next
		def _detailsFromModel(companyId: Int, details: Model) = {
			val oldId = details("id").getInt
			val addressModel = details("address").getModel
			val postalCodeModel = addressModel("postal_code").getModel
			val address = DbStreetAddress.getOrInsert(StreetAddressData(
				// FIXME: NoSuchElementException thrown
				postalCodeIdMap(postalCodeModel("county"))(postalCodeModel("code")),
				addressModel("street_name"), addressModel("building_number"), addressModel("stair"),
				addressModel("room_number")))
			val data = CompanyDetailsData(companyId, details("name"), address.id, details("tax_code"),
				created = details("created"), deprecatedAfter = details("deprecated_after"),
				isOfficial = details("is_official"))
			oldId -> data
		}
		
		// When processing new data:
		// Deprecates the imported details, unless they are official and the existing company details aren't
		def _deprecate(data: (Int, CompanyDetailsData)) = {
			if (data._2.isValid)
				data._1 -> data._2.copy(deprecatedAfter = Some(Now))
			else
				data
		}
		
		// Updates are in 3 parts:
		// 1) Option[Existing detail id to deprecate]
		// 2) Duplicates in form of [old detail id -> (new detail id, company id)]
		// 3) [old detail id -> Detail data to insert]
		val detailsUpdates = existingVersions.map { existingCompany =>
			val detailsData = detailsPerYCode(existingCompany.yCode).map { _detailsFromModel(existingCompany.id, _) }
			// Checks for possible duplicate data
			val (duplicateIdConversion, newData) = detailsData.divideWith { case (oldDetailsId, data) =>
				if (data ~== existingCompany.details.data)
					Left(oldDetailsId -> Pair(existingCompany.details.id, existingCompany.id))
				else
					Right(oldDetailsId -> data)
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
		// Details of new companies
		val newDetailsData = newCompanyYCodes.flatMap { yCode =>
			val companyId = companyIdPerYCode(yCode)
			val detailModels = detailsPerYCode(yCode)
			detailModels.map { _detailsFromModel(companyId, _) }
		}
		// Details added to old companies + those of new companies
		val allDetailsDataToInsert = (newDetailsData ++ detailsUpdates.flatMap { _._3 }).toVector
		val insertedDetails = CompanyDetailsModel.insert(allDetailsDataToInsert.map { _._2 })
		// Forms a map where keys are old detail ids and values are Pair(new detail id, company id)
		val newDetailIdPerOldId = insertedDetails.zip(allDetailsDataToInsert)
			.map { case (inserted, (oldId, _)) => oldId -> Pair(inserted.id, inserted.companyId) }.toMap ++
			detailsUpdates.flatMap { _._2 }
		
		// Returns this formed map + possible parsing failures
		newDetailIdPerOldId -> detailParseFailures
	}
	
	private def importCompanyProducts(companyModelPerYCode: Map[String, Model], companyIdPerYCode: Map[String, Int])
	                                 (implicit connection: Connection, context: DescriptionContext) =
	{
		// Imports all products as new (this may change once product codes have been introduced)
		// Parses product data from the models
		val (parseFailures, productModels) = companyModelPerYCode.splitFlatMap { case (yCode, companyModel) =>
			val companyId = companyIdPerYCode(yCode)
			val (failures, productModels) = companyModel("products").getVector.flatMap { _.model }
				.map { productSchema.validate(_) }.divided
			failures -> productModels.map { companyId -> _ }
		}
		// [(Product data, description models, old product id)]
		val productData = productModels.map { case (companyId, model) =>
			val data = CompanyProductData(companyId, model("unit_id"), model("default_unit_price"),
				model("tax_modifier"), created = model("created"), discontinuedAfter = model("discontinued_after"))
			val descriptionModels = model("descriptions").getVector.flatMap { _.model }
			(data, descriptionModels, model("id").getInt)
		}
		// Inserts product data to the DB
		val insertedProducts = CompanyProductModel.insert(productData.map { _._1 })
		val insertedProductsWithData = insertedProducts.zip(productData)
		// Inserts descriptions, also
		val descriptionFailures = importDescriptions(DbCompanyProductDescriptions,
			insertedProductsWithData
				.map { case (inserted, (_, descriptionModels, _)) => inserted.id -> descriptionModels }.toMap)
		// Returns old id -> new id -map, along with possible failures
		insertedProductsWithData.map { case (inserted, (_, _, oldId)) => oldId -> inserted.id }.toMap ->
			(parseFailures ++ descriptionFailures)
	}
	
	private def importCompanyBankAccounts(companyModelPerYCode: Map[String, Model],
	                                      companyIdPerYCode: Map[String, Int], bankIdPerBic: Map[String, Int])
	                                     (implicit connection: Connection) =
	{
		// Processes bank account models
		val (parseFailures, accountData) = companyModelPerYCode.splitFlatMap { case (yCode, model) =>
			val companyId = companyIdPerYCode(yCode)
			val (failures, accountModels) = model("bank_accounts").getVector.flatMap { _.model }
				.map { bankAccountSchema.validate(_) }.divided
			failures -> accountModels.map { model =>
				// TODO: There are many possibilities for failures here
				CompanyBankAccountData(companyId, bankIdPerBic(model("bic")), model("iban"), None, model("created"),
					model("deprecated_after"), model("is_official"))
			}
		}
		// Checks for existing bank accounts (doesn't import duplicates)
		val companyIds = accountData.map { _.companyId }.toSet
		val bankIds = accountData.map { _.bankId }.toSet
		val ibans = accountData.map { _.address }.toSet
		val existingAccounts = DbCompanyBankAccounts.belongingToAnyOfCompanies(companyIds)
			.inAnyOfBanks(bankIds).withAnyOfAddresses(ibans).pull
		val existingAccountsPerCompanyId = existingAccounts.groupBy { _.companyId }
		// Inserts the accounts which are new
		val insertedAccounts = CompanyBankAccountModel.insert(
			accountData.filterNot { data =>
				existingAccountsPerCompanyId.get(data.companyId)
					.exists { _.exists { existing =>
						existing.bankId == data.bankId && existing.address == data.address } } })
		// Returns a map for finding bank account ids + encountered failures
		// [bank id: [iban: account id]]
		val accountsMap = (existingAccounts ++ insertedAccounts).groupBy { _.bankId }
			.view.mapValues { _.map { account => account.address -> account.id }.toMap }.toMap
		accountsMap -> parseFailures
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
	
	private def importOrganizations(organizationModels: Vector[Model], companyIdPerYCode: Map[String, Int],
	                                userIdMap: Map[Int, Int])
	                               (implicit connection: Connection, context: DescriptionContext) =
	{
		val validUserRoleIds = DbUserRoles.ids.toSet
		// Reads information from each model
		// [([companyIds], [(userId, [userRoleIds])], [descriptionModels], [failures])]
		val organizationData = organizationModels.map { model =>
			// Reads organization members
			val (memberParseFailures, memberModels) = model("members").getVector.flatMap { _.model }
				.map { idSchema.validate(_) }.divided
			val (memberFailures, members) = memberModels.map { model =>
				userIdMap.get(model("id").getInt)
					.toTry { new NoSuchElementException(s"No user matches id ${model("id").getInt}") }
					.flatMap { userId =>
						val roleIds = model("role_ids").getVector.flatMap { _.int }.filter(validUserRoleIds.contains)
						if (roleIds.isEmpty)
							Failure(new IllegalArgumentException(
								s"role_ids must contain at least one valid value (provided: ${
									model("role_ids").getString})"))
						else
							Success(userId -> roleIds)
					}
			}.divided
			// Also includes company y-codes and descriptions
			val (yCodeFailures, companyIds) = model("company_codes").getVector.flatMap { _.string }
				.map { yCode => companyIdPerYCode.get(yCode)
					.toTry { new NoSuchElementException(s"No company for code $yCode") } }
				.divided
			(companyIds, members, model("descriptions").getVector.flatMap { _.model },
				memberParseFailures ++ memberFailures ++ yCodeFailures)
		}
		// Looks for existing organizations associated with specified companies
		val organizationCompanyIds = organizationData.flatMap { _._1 }.toSet
		val existingOrganizationCompanyLinks = DbOrganizationCompanies
			.linkedToAnyOfCompanies(organizationCompanyIds).pull
		val existingOrganizationIdsPerCompanyId = existingOrganizationCompanyLinks
			.map { link => link.companyId -> link.organizationId }.asMultiMap
		val existingCompanyIdsPerOrganizationId = existingOrganizationCompanyLinks
			.map { link => link.organizationId -> link.companyId }.asMultiMap
		// Also checks existing memberships among the listed users
		val existingUserIdsPerOrganizationId = DbMemberships
			.inAnyOfOrganizations(existingCompanyIdsPerOrganizationId.keys)
			.ofAnyOfUsers(userIdMap.valuesIterator.toSet)
			.pull.map { membership => membership.organizationId -> membership.userId }.asMultiMap
		// Divides the data into new organizations / companies and updates
		// Existing organization must be linked with one of the specified companies and must share one
		// common member
		val (updates, newOrganizationData) = organizationData
			.divideWith { case (companyIds, members, descriptionModels, _) =>
				companyIds.findMap { companyId => existingOrganizationIdsPerCompanyId.getOrElse(companyId, Vector())
					.find { organizationId => existingUserIdsPerOrganizationId.getOrElse(organizationId, Vector())
						.exists { userId => members.exists { _._1 == userId } } } }
				match {
					case Some(organizationId) => Left((organizationId, companyIds, members, descriptionModels))
					case None => Right(companyIds, members, descriptionModels)
				}
			}
		// Imports descriptions for existing organizations
		val descriptionUpdateFailures = importDescriptions(DbOrganizationDescriptions,
			updates.map { case (organizationId, _, _, descriptionModels) => organizationId -> descriptionModels }.toMap)
		// Collects new company- and member -links for the existing organizations
		val (updateCompanyLinks, updateMembershipData) = updates
			.splitFlatMap { case (organizationId, companyIds, members, _) =>
				val existingCompanyIds = existingCompanyIdsPerOrganizationId.getOrElse(organizationId, Vector())
				val existingUserIds = existingUserIdsPerOrganizationId.getOrElse(organizationId, Vector())
				val newCompanyLinks = companyIds.filterNot(existingCompanyIds.contains)
					.map { companyId => OrganizationCompanyData(organizationId, companyId) }
				val newMemberData = members.filterNot { case (userId, _) => existingUserIds.contains(userId) }
					.map { case (userId, roleIds) => MembershipData(organizationId, userId) -> roleIds }
				newCompanyLinks -> newMemberData
			}
		// Inserts new organizations to the DB
		val newOrganizationsWithData = OrganizationModel
			.insert(Vector.fill(newOrganizationData.size)(OrganizationData()))
			.zip(newOrganizationData)
		// Inserts descriptions for the new organizations
		val newDescriptionsFailures = importDescriptions(DbOrganizationDescriptions,
			newOrganizationsWithData.map { case (organization, (_, _ , descriptionModels)) =>
				organization.id -> descriptionModels }.toMap,
			checkForDuplicates = false)
		// Forms company- and member links for the new organizations
		val (newCompanyLinks, newMembershipData) = newOrganizationsWithData
			.splitFlatMap { case (organization, (companyIds, members, _)) =>
				companyIds.map { OrganizationCompanyData(organization.id, _) } ->
					members.map { case (userId, roleIds) => MembershipData(organization.id, userId) -> roleIds }
			}
		// Inserts the company links in a bulk
		OrganizationCompanyModel.insert(updateCompanyLinks ++ newCompanyLinks)
		// Inserts the memberships and then membership roles
		val allMembershipData = updateMembershipData ++ newMembershipData
		val insertedMemberships = MembershipModel.insert(allMembershipData.map { _._1 })
		val newMemberRoleData = insertedMemberships.zip(allMembershipData).flatMap { case (membership, (_, roleIds)) =>
			roleIds.map { roleId => MemberRoleLinkData(membership.id, roleId) }
		}
		MemberRoleLinkModel.insert(newMemberRoleData)
		
		// Returns all encountered failures
		organizationData.flatMap { _._4 } ++ descriptionUpdateFailures ++ newDescriptionsFailures
	}
	
	private def importInvoices(invoiceModels: Vector[Model], detailsIdMap: Map[Int, Pair[Int]],
	                           bankIdPerBic: Map[String, Int], accountsMap: Map[Int, Map[String, Int]],
	                           productIdMap: Map[Int, Int])
	                          (implicit connection: Connection, context: DescriptionContext) =
	{
		// Parses the main invoice part of each model
		val (parseFailures, validModels) = invoiceModels.map { invoiceSchema.validate(_) }.divided
		// (failures, [(Pair(sender company id, recipient company id), invoice data, invoice model)])
		val (invoiceFailures, invoiceData) = validModels.map { model =>
			detailsIdMap.get(model("sender_details_id"))
				.toTry { new NoSuchElementException(s"Invalid sender_details_id ${model("sender_details_id").getInt}") }
				.flatMap { case Pair(senderDetailsId, senderCompanyId) =>
					detailsIdMap.get(model("recipient_details_id"))
						.toTry { new NoSuchElementException(
							s"Invalid recipient_details_id ${model("recipient_details_id").getInt}") }
						.flatMap { case Pair(recipientDetailsId, recipientCompanyId) =>
							context.languageIdPerCode.get(model("language"))
								.toTry { new NoSuchElementException(s"Invalid language code ${model("language")}") }
								.flatMap { languageId =>
									val accountModel = model("sender_bank_account").getModel
									bankIdPerBic.get(accountModel("bic")).flatMap(accountsMap.get)
										.toTry { new NoSuchElementException(s"Unlisted bic ${accountModel("bic")}") }
										.flatMap { _.get(accountModel("iban")).toTry { new NoSuchElementException(
											s"Unlisted bank account $accountModel") } }
										.map { accountId =>
											val data = InvoiceData(senderDetailsId, recipientDetailsId, accountId,
												languageId, model("reference_code"),
												Days(model("payment_duration_days")),
												model("product_delivery").model.flatMap { DateRange(_).toOption },
												created = model("created"), cancelledAfter = model("cancelled_after"))
											(Pair(senderCompanyId, recipientCompanyId), data, model)
										}
								}
						}
				}
		}.divided
		// TODO: Filter out duplicates (based on company ids + reference code)
		// Inserts new invoices to DB
		val insertedInvoices = InvoiceModel.insert(invoiceData.map { _._2 })
		// Next parses all invoice items
		val (itemFailures, itemData) = insertedInvoices.zip(invoiceData.map { _._3 })
			.flatMap { case (invoice, invoiceModel) =>
				invoiceModel("items").getVector.flatMap { _.model }
					.map { invoiceItemSchema.validate(_).flatMap { model =>
						productIdMap.get(model("product_id"))
							.toTry { new NoSuchElementException(s"Invalid product id ${model("product_id")}") }
							.map { productId => InvoiceItemData(invoice.id, productId, model("description"),
								model("price_per_unit"), model("units_sold")) }
					} }
			}.divided
		// Inserts them to the database
		InvoiceItemModel.insert(itemData)
		
		// Returns encountered failures
		parseFailures ++ invoiceFailures ++ itemFailures
	}
	
	private def importDescriptionsFromModels(descriptionsAccess: LinkedDescriptionsAccess,
	                                         describedModels: Iterable[Model], checkForDuplicates: Boolean = true)
	                                        (implicit connection: Connection, context: DescriptionContext) =
	{
		// Processes the models
		val (failures, validModels) = describedModels.map { idSchema.validate(_) }.divided
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
				val (failures, valid) = models.map { descriptionModelSchema.validate(_) }.divided
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
