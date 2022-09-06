package vf.arbiter.command.app

import utopia.citadel.database.access.many.description.DbLanguageDescriptions
import utopia.citadel.database.access.many.language.{DbLanguageFamiliarities, DbLanguages}
import utopia.citadel.database.access.many.user.DbManyUserSettings
import utopia.citadel.database.access.single.description.DbLanguageDescription
import utopia.citadel.database.access.single.language.DbLanguage
import utopia.citadel.database.access.single.user.DbUser
import utopia.citadel.database.model.description.CitadelDescriptionLinkModel
import utopia.citadel.database.model.user.{UserLanguageLinkModel, UserModel, UserSettingsModel}
import utopia.citadel.model.enumeration.CitadelDescriptionRole.Name
import utopia.citadel.util.MetropolisAccessExtensions._
import utopia.flow.parse.Regex
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.metropolis.model.cached.LanguageIds
import utopia.metropolis.model.partial.description.DescriptionData
import utopia.metropolis.model.partial.user.{UserLanguageLinkData, UserSettingsData}
import utopia.vault.database.Connection
import vf.arbiter.command.model.cached.SelectedLanguage

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
	
	/**
	 * @param userId Id of the targeted user
	 * @param connection Implicit DB Connection
	 * @return Language list used by that user
	 */
	def validLanguageIdListForUserWithId(userId: Int)(implicit connection: Connection) =
	{
		val existingList = DbUser(userId).languageIds
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
	
	/**
	 * @param userId Id of the user for whom the language is being selected
	 * @param connection Implicit DB Connection
	 * @param languageIds Selectable language ids
	 * @return Selected language's id. None if no language ids were available or inserted or if user cancelled selection.
	 */
	def selectOrAddKnownLanguage(userId: Int)(implicit connection: Connection, languageIds: LanguageIds) =
	{
		// Reads language names
		val names = DbLanguageDescriptions.forPreferredLanguages.withRoleIdInPreferredLanguages(Name.id)
		// Asks for names for languages that don't have one
		val options: Vector[SelectedLanguage] = {
			if (names.size < languageIds.size)
			{
				def _nameForIdOrElse(id: Int)(backup: Int => String) = names.get(id) match
				{
					case Some(desc) => desc.description.text
					case None => backup(id)
				}
				val missingLanguages = DbLanguages(languageIds.filterNot(names.contains).toSet).pull
				def _missingLangCode(languageId: Int) = missingLanguages.find { _.id == languageId } match
				{
					case Some(language) => s"'${language.isoCode}'"
					case None => s"Language #$languageId"
				}
				
				println(s"There are ${languageIds.size - names.size} languages (${
					missingLanguages.map { _.isoCode }.sorted.mkString(", ")
				}) that don't have a name in any of your languages")
				if (StdIn.ask("Would you provide a name for those?"))
				{
					val primaryLanguageName = _nameForIdOrElse(languageIds.mostPreferred)(_missingLangCode)
					val newNames = missingLanguages.view.map { language =>
						StdIn.readNonEmptyLine(s"What's the name of ${language.isoCode} in $primaryLanguageName?")
							.map { language.id -> _ }
					}.takeWhile { _.isDefined }.flatten.toMap
					languageIds.map { id =>
						SelectedLanguage(id, _nameForIdOrElse(id) { id => newNames.getOrElse(id, _missingLangCode(id)) })
					}
				}
				else
					languageIds.map { id => SelectedLanguage(id, _nameForIdOrElse(id)(_missingLangCode)) }
			}
			else
				languageIds.map { id => SelectedLanguage(id, names(id).description.text) }
		}
		// Selects from the known languages (or inserts a new one)
		ActionUtils.selectOrInsert(options.map { l => l -> l.name }, "language") {
			StdIn.readNonEmptyLine("What's the 2 character ISO-code of your language (e.g. 'en')")
				.flatMap { code =>
					if (code.length != 2)
					{
						println(s"'$code' is not a valid language code (must be of length 2)")
						None
					}
					else
					{
						val (newLanguageIds, languageName) = addUserLanguage(userId, code, languageIds)
						languageName.map { SelectedLanguage(newLanguageIds.mostPreferred, _) }
					}
				}
		}
	}
	
	/**
	 * Adds a language to the languages known by the user
	 * @param userId Id of the user
	 * @param languageCode Language code (2-characters)
	 * @param existingLanguageIds Ids of the languages currently known by the user
	 * @param connection Implicit DB Connection
	 * @return Languages known to the user afterwards + name of the specified language
	 *         (which is None if that language was not added after all)
	 */
	def addUserLanguage(userId: Int, languageCode: String, existingLanguageIds: LanguageIds)
	                   (implicit connection: Connection): (LanguageIds, Option[String]) =
	{
		val language = DbLanguage.forIsoCode(languageCode).getOrInsert()
		implicit val languageIds: LanguageIds = existingLanguageIds.preferringLanguageWithId(language.id)
		// If this is already a user language, skips the rest
		if (existingLanguageIds.contains(language.id))
			existingLanguageIds ->
				Some(DbLanguageDescription(language.id).name.inPreferredLanguage match {
					case Some(nameDesc) => nameDesc.description.text
					case None => language.isoCode
				})
		else
		{
			val languageName: String = language.access.description.name.inLanguageWithId(language.id).text.orElse {
				// If the language didn't have a name yet, asks and inserts one
				StdIn.readNonEmptyLine(
					s"What's the name of '${language.isoCode}' in '${language.isoCode}'").map { name =>
					CitadelDescriptionLinkModel.language.insert(language.id,
						DescriptionData(Name.id, language.id, name, Some(userId)))
					name
				}
			}.getOrElse(languageCode)
			val proficiencyOptions = availableProficiencies(userId).sortBy { _.wrapped.orderIndex }.map { p =>
				p -> p(Name).getOrElse(p.wrapped.orderIndex.toString)
			}
			println(s"How proficient are you in $languageName?")
			ActionUtils.selectFrom(proficiencyOptions) match
			{
				case Some(proficiency) =>
					UserLanguageLinkModel.insert(UserLanguageLinkData(userId, language.id, proficiency.id))
					languageIds -> Some(languageName)
				case None =>
					println(s"$languageName won't be registered as one of your languages, then")
					existingLanguageIds -> None
			}
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
			val user = UserModel.insert()
			val settings = UserSettingsModel.insert(UserSettingsData(user.id, userName))
			implicit val languageIds: LanguageIds = setUserLanguages(user.id, languageCodes)
			
			// Creates a new company for that user (or joins an existing company)
			println(s"What's the name of your company?")
			println("Hint: If you wish to join an existing company, you can write part of that company's name")
			val company = StdIn.readNonEmptyLine().flatMap { CompanyActions.startOrJoin(user.id, _) }
			Some(settings -> company)
		}
	}
	
	private def findUserForName(userName: String)(implicit connection: Connection) =
		DbManyUserSettings.withName(userName).pull.bestMatch { _.email.isEmpty }.headOption
	
	private def setUserLanguages(userId: Int, languageCodes: Vector[String])(implicit connection: Connection) =
	{
		val languages = languageCodes.map { code => DbLanguage.forIsoCode(code).getOrInsert() }
		val languageNames: Map[Int, String] = languages.map { language =>
			val name = language.access.description.name.inLanguageWithId(language.id).text.getOrElse {
				// If the language didn't have a name yet, asks and inserts one
				val name = StdIn.readLineUntilNotEmpty(
					s"What's the name of '${language.isoCode}' in '${language.isoCode}'")
				CitadelDescriptionLinkModel.language.insert(language.id,
					DescriptionData(Name.id, language.id, name, Some(userId)))
				name
			}
			language.id -> name
		}.toMap
		implicit val languageIds: LanguageIds = LanguageIds(languages.map { _.id })
		val proficiencyOptions = availableProficiencies(userId).sortBy { _.wrapped.orderIndex }.map { p =>
			p -> p(Name).getOrElse(p.wrapped.orderIndex.toString)
		}
		val newProficiencyData = languages.map { language =>
			println(s"How proficient are you in ${languageNames(language.id)} (${language.isoCode})?")
			val proficiency = ActionUtils.forceSelectFrom(proficiencyOptions)
			language -> proficiency
		}
		UserLanguageLinkModel.insert(newProficiencyData
			.map { case (language, proficiency) => UserLanguageLinkData(userId, language.id, proficiency.id) })
		LanguageIds(newProficiencyData.sortBy { _._2.wrapped.orderIndex }.map { _._1.id })
	}
	
	private def availableProficiencies(userId: Int)(implicit connection: Connection, languageIds: LanguageIds) =
	{
		// Reads existing data
		val existingProficiencies = DbLanguageFamiliarities.described
		// Checks whether proficiency data can be named in the primary language
		if (languageIds.nonEmpty)
		{
			val languageId = languageIds.mostPreferred
			lazy val language = DbLanguage(languageId).withDescription(Name)
			
			// Reads proficiencies that are missing a name in the primary language
			val missingProficiencies = existingProficiencies
				.flatMap { p => p.description(Name).filter { _.languageId != languageId }.map { p -> _ } }
				.groupMap { _._2.languageId } { case (proficiency, desc) => proficiency -> desc.text }
			// Asks the user to provide translations for those proficiencies
			// Proficiency id => new proficiency name
			val newProficiencyNames = missingProficiencies.flatMap { case (languageId, proficiencies) =>
				language.toVector.flatMap { toLanguage =>
					val toLanguageName = toLanguage.descriptionOrCode(Name)
					DbLanguage(languageId).withDescription(Name).toVector.flatMap { fromLanguage =>
						val fromLanguageName = fromLanguage.descriptionOrCode(Name)
						if (StdIn.ask(s"There are ${
							proficiencies.size} language proficiencies in $fromLanguageName " +
							s"without $toLanguageName translation. Would you be willing to translate them?"))
							proficiencies.flatMap { case (proficiency, proficiencyName) =>
								StdIn.readNonEmptyLine(s"What's $proficiencyName in $toLanguageName?")
									.map { newName => proficiency.id -> newName }
							}
						else
							Vector()
					}
				}
			}
			if (newProficiencyNames.nonEmpty)
			{
				// Inserts the new descriptions
				// Proficiency id => new descriptions
				val insertedDescriptions = CitadelDescriptionLinkModel.languageFamiliarity.insertDescriptions(
					newProficiencyNames.map { case (profId, name) =>
						profId -> DescriptionData(Name.id, languageId, name, Some(userId))
					}.toVector).groupBy { _.targetId }
				// Returns updated proficiencies
				existingProficiencies.map { proficiency =>
					insertedDescriptions.get(proficiency.id) match
					{
						case Some(newDescriptions) =>
							proficiency.copy(descriptions = proficiency.descriptions
								.filterNot { d =>
									newDescriptions.exists { _.description.roleId == d.description.roleId }
								} ++ newDescriptions)
						case None => proficiency
					}
				}
			}
			else
				existingProficiencies
		}
		else
			Vector()
	}
}
