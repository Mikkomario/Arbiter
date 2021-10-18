package vf.arbiter.command.app

import utopia.citadel.database.access.many.description.DbLanguageDescriptions
import utopia.citadel.database.access.many.language.DbLanguages
import utopia.citadel.model.enumeration.CitadelDescriptionRole.Name
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.StringExtensions._
import utopia.metropolis.model.cached.LanguageIds
import utopia.vault.database.Connection
import vf.arbiter.command.model.SelectedLanguage

import java.time.LocalDate
import scala.io.StdIn

/**
 * Contains utility methods common to multiple action interfaces
 * @author Mikko Hilpinen
 * @since 11.10.2021, v0.1
 */
object ActionUtils
{
	/**
	 * Reads a date from input. Allows the user to reattempt input on parse failure.
	 * @param prompt Prompt to show before the first input request
	 * @return
	 */
	def readDate(prompt: String = "") =
	{
		prompt.notEmpty.foreach(println)
		println("Instruction: Supported formats are YYYY-MM-DD and DD.MM.YYYY")
		StdIn.readIterator.findMap[Option[LocalDate]] { v =>
			if (v.isEmpty)
				Some(None)
			else
				v.localDate match
				{
					case Some(d) => Some(Some(d))
					case None =>
						if (StdIn.ask(s"Couldn't convert '${v.getString}' to a date. Do you want to try again?"))
						{
							println("Please write the date again")
							None
						}
						else
							Some(None)
				}
		}.get
	}
	
	/**
	 * Forces the user to select one of their known language
	 * @param connection Implicit connection
	 * @param languageIds Ids of the languages known by the user (ordered) - Must not be empty
	 * @return Selected language's id
	 */
	def forceSelectKnownLanguage()(implicit connection: Connection, languageIds: LanguageIds) =
		selectKnownLanguage(force = true).get
	/**
	 * @param force Whether selection should be forced (default = false)
	 * @param connection Implicit DB Connection
	 * @param languageIds Selectable language ids
	 * @return Selected language's id. None if no language ids were available or if user cancelled selection.
	 */
	def selectKnownLanguage(force: Boolean = false)(implicit connection: Connection, languageIds: LanguageIds) =
	{
		// Reads language names
		val names = DbLanguageDescriptions.forPreferredLanguages.withRoleIdInPreferredLanguages(Name.id)
		// Asks for names for languages that don't have one
		val options: Vector[(Int, String)] = {
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
						id -> _nameForIdOrElse(id) { id => newNames.getOrElse(id, _missingLangCode(id)) }
					}
				}
				else
					languageIds.map { id => id -> _nameForIdOrElse(id)(_missingLangCode) }
			}
			else
				languageIds.map { id => id -> names(id).description.text }
		}
		// Selects from the known languages
		val selectedId = {
			if (force && languageIds.nonEmpty)
				Some(forceSelectFrom(options))
			else
				selectFrom(options, "languages")
		}
		// Attaches language name to the result
		selectedId.flatMap { id => options.find { _._1 == id }.map { case (_, name) => SelectedLanguage(id, name) } }
	}
	
	/**
	 * Allows the user to select from 0-n options
	 * @param options Options for the user to select from. Option descriptions are on the right side.
	 * @param target word used for the selected items (plural) (default = items)
	 * @param verb Verb used for the selection action (default = select)
	 * @param skipQuestion Whether the question: "Do you want to use one of these...?" should be skipped
	 *                     (default = false)
	 * @tparam A Type of the selected item
	 * @return Item selected by the user. None if there were no items to select from or if the user didn't want to
	 *         select any of them.
	 */
	def selectFrom[A](options: Seq[(A, String)], target: String = "items", verb: String = "select",
	                  skipQuestion: Boolean = false): Option[A] =
	{
		if (options.isEmpty)
		{
			println(s"No $target found")
			None
		}
		else if (options.size == 1)
		{
			if (StdIn.ask(s"Found ${options.head._2}. Do you want to $verb it?"))
				Some(options.head._1)
			else
				None
		}
		else
		{
			if (skipQuestion)
				_selectFrom(options)
			else
			{
				val bestNameMatches = options.map { _._2 }.sortBy { _.length }.take(3)
				println(s"Found ${options.size} $target: ${bestNameMatches.mkString(", ")}${
					if (options.size > bestNameMatches.size) "..." else ""}")
				if (StdIn.ask(s"Do you want to $verb one of these $target?"))
					_selectFrom(options)
				else
					None
			}
		}
	}
	
	/**
	 * Makes the user select from the specified items - requires an answer
	 * @param options Options to select from. Must not be empty.
	 * @tparam A Type of item being selected
	 * @return The selected item
	 */
	// Options must be of size 1 or more
	def forceSelectFrom[A](options: Seq[(A, String)]): A =
	{
		def _narrow(filter: String): A =
		{
			val narrowed = options.filter { _._2.toLowerCase.contains(filter) }
			if (narrowed.isEmpty)
			{
				println("No results could be found with that filter, please try again")
				forceSelectFrom(options)
			}
			else
				narrowed.find { _._2 ~== filter }.map { _._1 }.getOrElse { forceSelectFrom(narrowed) }
		}
		
		if (options.size == 1)
		{
			val (result, resultName) = options.head
			println(s"Found $resultName")
			result
		}
		else if (options.size > 10)
		{
			println(s"Found ${options.size} options")
			val filter = StdIn.readLineUntilNotEmpty(
				"Please narrow the selection by specifying an additional filter").toLowerCase
			_narrow(filter)
		}
		else
		{
			println(s"Found ${options.size} options")
			options.indices.foreach { index => println(s"${index + 1}: ${options(index)._2}") }
			println("Please select the correct index or narrow the selection by typing text")
			StdIn.readIterator.filter { _.isDefined }.findMap { input =>
				input.int match
				{
					case Some(index) =>
						if (index > 0 && index <= options.size)
							Some(Right(index))
						else
						{
							println("That index is out of range, please select a new one")
							None
						}
					case None => Some(Left(input.getString))
				}
			}.get match
			{
				case Right(index) => options(index - 1)._1
				case Left(filter) => _narrow(filter)
			}
		}
	}
	
	// This variant of select allows for cancelling
	// Options mustn't be empty
	private def _selectFrom[A](options: Seq[(A, String)]): Option[A] =
	{
		def _narrow(filter: String): Option[A] =
		{
			val narrowed = options.filter { _._2.toLowerCase.contains(filter) }
			if (narrowed.isEmpty)
			{
				println("No results could be found with that filter, please try again")
				_selectFrom(options)
			}
			else
				narrowed.find { _._2 ~== filter }.map { _._1 }.orElse { _selectFrom(narrowed) }
		}
		
		if (options.size == 1)
		{
			val (result, resultName) = options.head
			println(s"Found $resultName")
			Some(result)
		}
		else if (options.size > 10)
		{
			println(s"Found ${options.size} options")
			StdIn.readNonEmptyLine(
				"Please narrow the selection by specifying an additional filter (empty cancels)")
				.flatMap { filter => _narrow(filter.toLowerCase) }
		}
		else
		{
			println(s"Found ${options.size} options")
			options.indices.foreach { index => println(s"${index + 1}: ${options(index)._2}") }
			println("Please select the correct index or narrow the selection by typing text (empty cancels)")
			StdIn.readIterator.findMap { input =>
				input.int match
				{
					// Case: User typed a row index => makes sure it is in range
					case Some(index) =>
						if (index > 0 && index <= options.size)
							Some(Some(Right(index)))
						else
						{
							println("That index is out of range, please select a new one")
							None
						}
					// Case: User typed text or nothing => Returns that
					case None => Some(input.string.map { Left(_) })
				}
			}.get.flatMap {
				case Right(index) => Some(options(index - 1)._1)
				case Left(filter) => _narrow(filter)
			}
		}
	}
}
