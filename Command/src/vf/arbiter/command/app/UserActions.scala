package vf.arbiter.command.app

import utopia.citadel.database.access.many.description.DbDescriptions
import utopia.citadel.database.access.many.language.DbLanguageFamiliarities
import utopia.citadel.database.access.many.user.DbManyUserSettings
import utopia.citadel.database.access.single.description.DbDescription
import utopia.citadel.database.access.single.language.DbLanguage
import utopia.citadel.database.model.description.DescriptionLinkModel
import utopia.citadel.database.model.user.{UserLanguageModel, UserModel}
import utopia.citadel.model.enumeration.CitadelDescriptionRole.Name
import utopia.flow.parse.Regex
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.metropolis.model.partial.description.DescriptionData
import utopia.metropolis.model.partial.user.{UserLanguageData, UserSettingsData}
import utopia.metropolis.model.stored.user.User
import utopia.vault.database.Connection

import scala.io.StdIn

/**
 * Contains interactive actions related to users
 * @author Mikko Hilpinen
 * @since 10.10.2021, v0.1
 */
object UserActions
{
	private val notLetterRegex = !Regex.alpha
	
	/**
	 * Registers a new user, if possible
	 * @param userName Name of the new user
	 * @param connection Implicit DB connection
	 * @return New user (may be None) + linked company (may be None)
	 */
	def register(userName: String)(implicit connection: Connection) =
		findUserForName(userName) match
		{
			// Case: User already exists => may login as that user
			case Some(existingUser) =>
				println("User with that name already exists")
				if (StdIn.ask("Do you want to resume as that user?"))
					Some(existingUser) -> None
				else
					None -> None
			// Case: User doesn't exist => creates one
			case None =>
				val (user, company) = _register(userName)
				Some(user) -> company
		}
	
	/**
	 * Logs in with the specified user name. If no user was found, allows the user to register a new user.
	 * @param userName User name
	 * @param connection Implicit connection
	 * @return New user + created company (if registered, None otherwise).
	 *         None if no user was found and the user didn't want to register a new user.
	 */
	def loginAs(userName: String)(implicit connection: Connection) =
		findUserForName(userName).map { _ -> None }.orElse {
			if (StdIn.ask(
				s"No user was found with that name. Do you want to register '$userName' as a new user?"))
				Some(_register(userName))
			else
				None
		}
	
	private def _register(userName: String)(implicit connection: Connection) =
	{
		// Registers the user
		val user = UserModel.insert(UserSettingsData(userName))
		
		// Registers language proficiencies for the user
		println("What languages do you know?")
		println("Instruction: Use 2-letter ISO-codes like 'en'. Insert all codes on the same line.")
		val languageCodes = notLetterRegex.split(StdIn.readLineUntilNotEmpty())
			.toVector.filter { _.nonEmpty }.map { _.toLowerCase }
		val languages = languageCodes.map { code => DbLanguage.forIsoCode(code).getOrInsert() }
		val languageNames = languages.map { language =>
			language.id -> DbDescription.ofLanguageWithId(language.id).name.inLanguageWithId(language.id).getOrElse {
				// If the language didn't have a name yet, asks and inserts one
				val name = StdIn.readLineUntilNotEmpty(
					s"What's the name of '${language.isoCode}' in '${language.isoCode}'")
				DescriptionLinkModel.language.insert(language.id,
					DescriptionData(Name.id, language.id, name, Some(user.id)))
				name
			}
		}.toMap
		// TODO: This part could use some refactoring for sure
		val languageOrder = languages.map { _.id }
		val availableProficiencies = DbLanguageFamiliarities.all
		val proficiencyNames = DbDescriptions
			.ofLanguageFamiliaritiesWithIds(availableProficiencies.map { _.id }.toSet)
			.forRoleInLanguages(Name.id, languageOrder)
			.view.mapValues { _.description.text }.toMap
		val proficiencyOptions =
		{
			if (proficiencyNames.isEmpty)
				availableProficiencies.sortBy { -_.orderIndex }.map { p => p -> p.orderIndex.toString }
			else
				availableProficiencies.flatMap { p => proficiencyNames.get(p.id).map { p -> _ } }
		}
		val newProficiencyData = languages.map { language =>
			println(s"How proficient are you in ${languageNames(language.id)} (${language.isoCode})?")
			val proficiency = ActionUtils.forceSelectFrom(proficiencyOptions)
			language -> proficiency
		}
		UserLanguageModel.insert(newProficiencyData
			.map { case (language, proficiency) => UserLanguageData(user.id, language.id, proficiency.id) })
		val mostProficientLanguage = newProficiencyData.maxBy { _._2 }._1
		
		// Creates a new company for that user (or joins an existing company)
		println(s"What's the name of your company? (in ${languageNames(mostProficientLanguage.id)})")
		println("Hint: If you wish to join an existing company, you can write part of that company's name")
		val company = StdIn.readNonEmptyLine().flatMap { CompanyActions.startOrJoin(user.id, _) }
		user -> company
	}
	
	private def findUserForName(userName: String)(implicit connection: Connection) =
		DbManyUserSettings.withName(userName).bestMatch(Vector(_.email.isEmpty))
			.headOption.map { s => User(s.userId, s) }
}
