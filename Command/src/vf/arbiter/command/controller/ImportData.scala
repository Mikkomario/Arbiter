package vf.arbiter.command.controller

import utopia.bunnymunch.jawn.JsonBunny
import utopia.citadel.database.access.many.description.{DbDescriptionRoles, DbLanguageDescriptions, LinkedDescriptionsAccess}
import utopia.citadel.database.access.many.language.DbLanguages
import utopia.citadel.database.access.many.user.DbManyUserSettings
import utopia.citadel.database.model.language.LanguageModel
import utopia.citadel.database.model.user.{UserModel, UserSettingsModel}
import utopia.flow.datastructure.immutable.{Model, ModelDeclaration}
import utopia.flow.generic.{IntType, StringType}
import utopia.flow.util.CollectionExtensions._
import utopia.metropolis.model.partial.description.DescriptionData
import utopia.metropolis.model.partial.language.LanguageData
import utopia.metropolis.model.partial.user.{UserData, UserSettingsData}
import utopia.metropolis.model.stored.description.DescriptionRole
import utopia.vault.database.Connection

import java.nio.file.Path

/**
 * Used for importing previously exported data
 * @author Mikko Hilpinen
 * @since 13.11.2021, v1.2
 */
object ImportData
{
	private val descriptionModelSchema = ModelDeclaration("language" -> StringType)
	private val languageSchema = ModelDeclaration("code" -> StringType)
	private val userSchema = ModelDeclaration("id" -> IntType, "name" -> StringType)
	
	def fromJson(path: Path)(implicit connection: Connection) = JsonBunny.munchPath(path).map { root =>
		val descriptionRoles = DbDescriptionRoles.pull
		// Imports languages
		val (languageIdPerCode, languageFailures) = importLanguages(root("languages").getVector.flatMap { _.model },
			descriptionRoles)
		// Imports users. Remembers which user was linked with which id
		// UserIdMap is { old user id: new user id }
		val (userIdMap, userFailures) = importUsers(root("users").getVector.flatMap { _.model })
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
		val firstDescriptionFailures = importDescriptions(DbLanguageDescriptions,
			newCases.map { case (code, descriptions) => languageIdPerCode(code) -> descriptions },
			languageIdPerCode, descriptionRoles, checkForDuplicates = false)
		val secondDescriptionFailures = importDescriptions(DbLanguageDescriptions,
			existingCases.map { case (code, descriptions) => languageIdPerCode(code) -> descriptions },
			languageIdPerCode, descriptionRoles)
		
		// Returns language id per code -map + possible failures
		languageIdPerCode -> (failures ++ firstDescriptionFailures ++ secondDescriptionFailures)
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
	
	private def importDescriptions(descriptionsAccess: LinkedDescriptionsAccess,
	                               descriptionModelsPerId: Map[Int, Vector[Model]],
	                               languageIdPerCode: Map[String, Int], descriptionRoles: Iterable[DescriptionRole],
	                               checkForDuplicates: Boolean = true)
	                              (implicit connection: Connection) =
	{
		if (descriptionModelsPerId.nonEmpty) {
			// Parses specified description models into description data. Collects possible failures, also.
			val (failures, proposedDescriptions) = descriptionModelsPerId.view.mapValues { models =>
				val (failures, valid) = models.map { descriptionModelSchema.validate(_).toTry }.divided
				val (languageFailures, descriptions) = valid.flatDivideWith { model => {
					// The specified language code must be valid (map to an id)
					val languageCode = model("language").getString
					languageIdPerCode.get(languageCode) match {
						case Some(languageId) =>
							val descriptionsModel = model("descriptions").getModel
							// Looks for descriptions for each description role (singular and plural versions)
							descriptionRoles.flatMap { role =>
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
}
