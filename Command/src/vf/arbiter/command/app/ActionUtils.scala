package vf.arbiter.command.app

import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.StringExtensions._

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
	 * Selects an existing item or allows the user to insert one
	 * @param options Existing options [(item + description)]
	 * @param target Singular name for the target item
	 * @param skipInsertQuestion Whether question to insert should be skipped when there are 0 options to select from
	 *                           (default = false)
	 * @param insert A function for inserting a new item
	 * @tparam A Type of selected item
	 * @return The selected or inserted item
	 */
	def selectOrInsert[A](options: Seq[(A, String)], target: String = "item", skipInsertQuestion: Boolean = false)
	                     (insert: => Option[A]) =
	{
		if (options.isEmpty)
		{
			if (skipInsertQuestion || StdIn.ask(s"Do you want to create a new $target?", default = true))
				insert
			else
				None
		}
		else
			_selectFrom(options, Some(() => insert))
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
				_selectFrom(options, None)
			else
			{
				val bestNameMatches = options.map { _._2 }.sortBy { _.length }.take(3)
				println(s"Found ${options.size} $target: ${bestNameMatches.mkString(", ")}${
					if (options.size > bestNameMatches.size) "..." else ""}")
				if (StdIn.ask(s"Do you want to $verb one of these $target?"))
					_selectFrom(options, None)
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
	private def _selectFrom[A](options: Seq[(A, String)], insert: Option[() => Option[A]]): Option[A] =
	{
		def _narrow(filter: String): Option[A] =
		{
			val narrowed = options.filter { _._2.toLowerCase.contains(filter) }
			if (narrowed.isEmpty)
			{
				println("No results could be found with that filter, please try again")
				_selectFrom(options, insert)
			}
			else
				narrowed.find { _._2 ~== filter }.map { _._1 }.orElse { _selectFrom(narrowed, insert) }
		}
		
		if (options.size == 1)
		{
			val (result, resultName) = options.head
			insert match
			{
				case Some(insert) =>
					if (StdIn.ask(s"Do you want to select $resultName?", default = true))
						Some(result)
					else if (StdIn.ask("Do you want to insert a new item instead?"))
						insert()
					else
						None
				case None =>
					println(s"Found $resultName")
					Some(result)
			}
		}
		else if (options.size > 10)
		{
			println(s"Found ${options.size} options")
			println("Please narrow the selection by specifying an additional filter (empty cancels)")
			if (insert.nonEmpty)
				println("Hint: you can also insert a new item by typing 'new'")
			StdIn.readNonEmptyLine().flatMap { filter =>
				insert match
				{
					case Some(insert) =>
						if (filter.toLowerCase == "new")
							insert()
						else
							_narrow(filter.toLowerCase)
					case None => _narrow(filter.toLowerCase)
				}
			}
		}
		else
		{
			println(s"Found ${options.size} options")
			options.indices.foreach { index => println(s"${index + 1}: ${options(index)._2}") }
			if (insert.isDefined)
				println("0: Create new")
			println("Please select the correct index or narrow the selection by typing text (empty cancels)")
			StdIn.readIterator.findMap { input =>
				input.int match
				{
					// Case: User typed a row index => makes sure it is in range
					case Some(index) =>
						if (index == 0 && insert.isDefined)
							Some(Some(Right(index)))
						else if (index > 0 && index <= options.size)
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
				case Right(index) =>
					if (index == 0)
						insert.flatMap { _() }
					else
						Some(options(index - 1)._1)
				case Left(filter) => _narrow(filter)
			}
		}
	}
}
