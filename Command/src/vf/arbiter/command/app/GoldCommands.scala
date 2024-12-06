package vf.arbiter.command.app

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.TryExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.console.{ArgumentSchema, Command, CommandArguments}
import utopia.vault.database.Connection
import vf.arbiter.core.util.Common._
import vf.arbiter.gold.model.cached.price.Price
import vf.arbiter.gold.model.enumeration.Currency

import scala.io.StdIn

/**
 * Interface for accessing gold-related console commands
 * @author Mikko Hilpinen
 * @since 17.9.2023, v1.4
 */
object GoldCommands
{
	// ATTRIBUTES   ---------------------------
	
	private lazy val currencyArg = ArgumentSchema("currency", "c", "EUR",
		help = "Currency in which monetary values are measured")
	private lazy val periodArg = ArgumentSchema("averagePeriod", "t", 30,
		help = "Number of days for which the average metal values are calculated now and then")
	
	/**
	 * Command for requesting current value of gold
	 */
	lazy val currentGoldPrice = Command("goldvalue", "gold", "Shows the recent average value of gold in some currency")(
		currencyArg, periodArg) { args =>
		withConnection { implicit c => GoldActions.printCurrentGoldPrice(currencyFrom(args), periodFrom(args)) }
	}
	/**
	 * Command for converting euros to gold
	 */
	lazy val valueOf = Command("valueof", "togold", "Shows the current value of a specific monetary amount in gold")(
		ArgumentSchema("fiat", help = "The amount of (fiat) currency to convert to gold"), currencyArg, periodArg) {
		args =>
			val currency = currencyFrom(args)
			args("fiat").double
				.orElse { StdIn.read(s"Please specify the amount of $currency to convert to gold").double } match
			{
				case Some(amount) =>
					withConnection { implicit c =>
						GoldActions.currentGoldValueOf(Price(amount, currency), periodFrom(args))
					}
				case None => println("Cancelled")
			}
	}
	/**
	 * Command for correcting a previous euro value according to changes in gold price since then
	 */
	lazy val correctPrice = Command("correct", "cor",
		help = "Corrects the value of a previously agreed sum against inflation using gold (and possibly silver) price data.")(
		ArgumentSchema("original", "o", help = "The original monetary amount"), currencyArg,
		ArgumentSchema("date", "d", help = "Date when the specified amount was valid"),
		periodArg,
		ArgumentSchema.flag("includeSilver", "S",
			"Whether both gold and silver should be used for determining the rate of inflation (by default, only gold is used)")) {
		args =>
			implicit val currency: Currency = currencyFrom(args)
			args("original").double
				.orElse {
					StdIn.read(s"Please specify the original value in $currency to convert to current date value")
						.double
				} match
			{
				case Some(originalAmount) =>
					args("date").localDate
						.orElse { StdIn.read("Please specify the date on which that price was valid").localDate } match
					{
						case Some(originalDate) =>
							GoldActions.determineCurrentPrice(originalAmount, originalDate, periodFrom(args),
								includeSilver = args("includeSilver").getBoolean)
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
	
	private def currencyFrom(args: CommandArguments) = Currency.fromValue(args("currency"))
	private def periodFrom(args: CommandArguments) = args("averagePeriod").intOr(30).days
}
