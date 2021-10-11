package vf.arbiter.command.app

import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.StringExtensions._

import scala.io.StdIn

/**
 * Contains utility methods common to multiple action interfaces
 * @author Mikko Hilpinen
 * @since 11.10.2021, v0.1
 */
object ActionUtils
{
	/**
	 * Allows the user to select from 0-n options
	 * @param options Options for the user to select from. Option descriptions are on the right side.
	 * @param target word used for the selected items (plural) (default = items)
	 * @param verb Verb used for the selection action (default = select)
	 * @tparam A Type of the selected item
	 * @return Item selected by the user. None if there were no items to select from or if the user didn't want to
	 *         select any of them.
	 */
	def selectFrom[A](options: Seq[(A, String)], target: String = "items", verb: String = "select"): Option[A] =
	{
		if (options.isEmpty)
		{
			println("No matches found")
			None
		}
		else
		{
			val bestNameMatches = options.map { _._2 }.sortBy { _.length }.take(3)
			println(s"Found ${options.size} matches: ${bestNameMatches.mkString(", ")}${
				if (options.size > bestNameMatches.size) "..." else ""}")
			if (StdIn.ask(s"Do you want to $verb one of these $target?"))
				Some(forceSelectFrom(options))
			else
				None
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
}
