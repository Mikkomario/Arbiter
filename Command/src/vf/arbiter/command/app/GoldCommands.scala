package vf.arbiter.command.app

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.console.{ArgumentSchema, Command}
import utopia.flow.util.console.ConsoleExtensions._
import utopia.vault.database.Connection
import vf.arbiter.core.util.Common._

import scala.io.StdIn

/**
 * Interface for accessing gold-related console commands
 * @author Mikko Hilpinen
 * @since 17.9.2023, v1.4
 */
object GoldCommands
{
	// ATTRIBUTES   ---------------------------
	
	private lazy val periodArgSchema = ArgumentSchema("averagePeriod", "t", 30,
		help = "Number of days for which the average metal values are calculated now and then")
	
	/**
	 * Command for requesting current value of gold
	 */
	lazy val currentGoldPrice = Command("goldvalue", "gold", "Shows the recent average value of gold in Euros")(
		periodArgSchema) { args =>
		withConnection { implicit c =>
			GoldActions.printCurrentGoldPrice(args("averagePeriod").intOr(30).days)
		}
	}
	/**
	 * Command for converting euros to gold
	 */
	lazy val valueOf = Command("valueof", "togold", "Shows the current value of a specific Euro amount in gold")(
		ArgumentSchema("euros", "euro", help = "The amount of euros to convert to gold"),
		periodArgSchema) { args =>
		args("euros").double
			.orElse { StdIn.read("Please specify the amount of Euros to convert to gold").double } match
		{
			case Some(euros) =>
				withConnection { implicit c =>
					GoldActions.currentGoldValueOfEuros(euros, args("averagePeriod").intOr(30).days)
				}
			case None => println("Cancelled")
		}
	}
	/**
	 * Command for correcting a previous euro value according to changes in gold price since then
	 */
	lazy val correctPrice = Command("correct", "cor",
		help = "Corrects the value of a previously agreed sum against inflation using gold (and possibly silver) price data.")(
		ArgumentSchema("original", "o", help = "The original amount in Euros"),
		ArgumentSchema("date", "d", help = "Date when the specified Euro amount was valid"),
		periodArgSchema,
		ArgumentSchema.flag("includeSilver", "S",
			"Whether both gold and silver should be used for determining the rate of inflation (by default, only gold is used)")) { args =>
		args("original").double
			.orElse {
				StdIn.read("Please specify the original value in Euros to convert to current date value").double
			} match {
			case Some(originalEuros) =>
				args("date").localDate
					.orElse { StdIn.read("Please specify the date on which that price was valid").localDate } match {
					case Some(originalDate) => GoldActions.determineCurrentPrice(originalEuros, originalDate,
						args("averagePeriod").intOr(30).days, includeSilver = args("includeSilver").getBoolean)
					case None => println("Cancelled")
				}
			case None => println("Cancelled")
		}
	}
	
	
	// COMPUTED -------------------------------
	
	/**
	 * @return All gold-related commands
	 */
	def all = Vector(currentGoldPrice, valueOf, correctPrice)
	
	
	// OTHER    -------------------------------
	
	// Opens a DB connection and prints failures
	private def withConnection[U](f: Connection => U) = {
		connectionPool.tryWith(f).failure.foreach { error =>
			error.printStackTrace()
			println(s"Database interactions failed (${
				error.getMessage}). \nTerminates command execution. \nPlease see the error above.")
		}
	}
}
