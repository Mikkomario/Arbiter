package vf.arbiter.command.app

import utopia.citadel.database.access.many.language.DbLanguageFamiliarities
import utopia.citadel.database.access.many.user.DbManyUserSettings
import utopia.citadel.database.access.single.language.DbLanguage
import utopia.citadel.database.access.single.user.DbUser
import utopia.citadel.database.model.description.DescriptionLinkModel
import utopia.citadel.database.model.user.{UserLanguageModel, UserModel}
import utopia.citadel.model.enumeration.CitadelDescriptionRole.Name
import utopia.citadel.util.MetropolisAccessExtensions._
import utopia.flow.parse.Regex
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.metropolis.model.cached.LanguageIds
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
				 _register(userName) match
				 {
					 case Some((user, company)) => Some(user) -> company
					 case None => None -> None
				 }
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
				_register(userName)
			else
				None
		}
	
	def validLanguageIdListForUserWithId(userId: Int)(implicit connection: Connection) =
	{
		val existingList = DbUser(userId).languageIdsList
		if (existingList.nonEmpty)
			existingList
		else
		{
			println("Please write a list of the languages you know")
			println("Instruction: Each language should be written as a two letter ISO-code (like 'en')")
			val languageCodes = StdIn.readLineIterator.flatMap { line =>
				val codes = notLetterRegex.split(line).toVector.filter { _.length == 2 }.map { _.toLowerCase }
				if (codes.isEmpty)
				{
					println(s"Couldn't find a single language code from input '$line'. Please try again.")
					None
				}
				else if (codes.contains("en") ||
					StdIn.ask("You didn't specify english (en) as a language. Are you sure you want to continue anyway?"))
					Some(codes)
				else
				{
					println("Please write a new set of language codes")
					None
				}
			}.next()
			setUserLanguages(userId, languageCodes)
		}
	}
	
	private def _register(userName: String)(implicit connection: Connection) =
	{
		// Registers language proficiencies for the user
		println("What languages do you know?")
		println("Instruction: Use 2-letter ISO-codes like 'en'. Insert all codes on the same line.")
		val languageCodes = notLetterRegex.split(StdIn.readLine())
			.toVector.filter { _.length == 2 }.map { _.toLowerCase }
		if (languageCodes.isEmpty)
		{
			println("Looks like you didn't specify any language codes. " +
				"Unfortunately you can't create a user account without doing so. Please try again later.")
			None
		}
		else
		{
			// Registers the user and the language data
			val user = UserModel.insert(UserSettingsData(userName))
			implicit val languageIds: LanguageIds = setUserLanguages(user.id, languageCodes)
			
			// Creates a new company for that user (or joins an existing company)
			println(s"What's the name of your company?")
			println("Hint: If you wish to join an existing company, you can write part of that company's name")
			val company = StdIn.readNonEmptyLine().flatMap { CompanyActions.startOrJoin(user.id, _) }
			Some(user -> company)
		}
	}
	
	private def findUserForName(userName: String)(implicit connection: Connection) =
		DbManyUserSettings.withName(userName).bestMatch(Vector(_.email.isEmpty))
			.headOption.map { s => User(s.userId, s) }
	
	private def setUserLanguages(userId: Int, languageCodes: Vector[String])(implicit connection: Connection) =
	{
		val languages = languageCodes.map { code => DbLanguage.forIsoCode(code).getOrInsert() }
		val languageNames: Map[Int, String] = languages.map { language =>
			val name = language.access.description.name.inLanguageWithId(language.id).text.getOrElse {
				// If the language didn't have a name yet, asks and inserts one
				val name = StdIn.readLineUntilNotEmpty(
					s"What's the name of '${language.isoCode}' in '${language.isoCode}'")
				DescriptionLinkModel.language.insert(language.id,
					DescriptionData(Name.id, language.id, name, Some(userId)))
				name
			}
			language.id -> name
		}.toMap
		implicit val languageIds: LanguageIds = LanguageIds(languages.map { _.id })
		val availableProficiencies = DbLanguageFamiliarities.described
		val proficiencyOptions = availableProficiencies.sortBy { _.orderIndex }.map { p =>
			p -> p(Name).getOrElse(p.orderIndex.toString)
		}
		val newProficiencyData = languages.map { language =>
			println(s"How proficient are you in ${languageNames(language.id)} (${language.isoCode})?")
			val proficiency = ActionUtils.forceSelectFrom(proficiencyOptions)
			language -> proficiency
		}
		UserLanguageModel.insert(newProficiencyData
			.map { case (language, proficiency) => UserLanguageData(userId, language.id, proficiency.id) })
		LanguageIds(newProficiencyData.sortBy { _._2.wrapped.orderIndex }.map { _._1.id })
	}
}
