package vf.arbiter.command.app

import utopia.flow.async.AsyncExtensions._
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.time.TimeExtensions._
import utopia.flow.time.{DateRange, Days, Today}
import utopia.flow.util.{StringUtils, TryCatch}
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.TryExtensions._
import utopia.flow.view.immutable.caching.ConditionalLazy
import utopia.vault.database.Connection
import vf.arbiter.core.util.Common._
import vf.arbiter.gold.controller.price.{CorrectInflation, MetalPrices}
import vf.arbiter.gold.controller.settings.ArbiterGoldSettings
import vf.arbiter.gold.model.cached.auth.ApiKey
import vf.arbiter.gold.model.cached.price.{Price, WeightPrice}
import vf.arbiter.gold.model.enumeration.Currency.Euro
import vf.arbiter.gold.model.enumeration.Metal.{Gold, Silver}
import vf.arbiter.gold.model.enumeration.{Currency, WeightUnit}

import java.nio.file.Path
import java.time.LocalDate
import scala.io.{Codec, StdIn}
import scala.util.{Failure, Success}

/**
 * Provides interactive methods for gold-related functions
 * @author Mikko Hilpinen
 * @since 15.9.2023, v1.4
 */
object GoldActions
{
	// ATTRIBUTES   --------------------------------
	
	private implicit val codec: Codec = Codec.UTF8
	
	private val defaultReferencePeriod = Days(30)
	
	private val apiKeyCache = ConditionalLazy {
		val result = connectionPool.tryWith { implicit c =>
			// Reads the key from DB if possible
			ArbiterGoldSettings.apiKey.orElse {
				// If not stored in the DB, requests the user for a new key
				StdIn.readNonEmptyLine(
					"Please specify the API-key for the metal prices API.\nIf you don't have an API-key yet, visit https://metalpriceapi.com/ to setup a free account.")
					.map { key =>
						println("When using a free Metal Prices API plan, this software has to comply with the plan limits.")
						val apiKey = ApiKey(key, StdIn.ask("Do you have a paid Metal Prices API plan?"))
						
						// Remembers the API key
						ArbiterGoldSettings.apiKey = apiKey
						
						apiKey
					}
			}
		}
		result -> result.toOption.exists { _.isDefined }
	}
	
	
	// OTHER    ------------------------------------
	
	/**
	 * Requests and prints the current average price of gold
	 * @param referencePeriod Duration for the average calculation. Default = 30 days.
	 * @param connection Implicit DB Connection
	 */
	def printCurrentGoldPrice(currency: Currency, referencePeriod: Days = defaultReferencePeriod)
	                         (implicit connection: Connection) =
	{
		forCurrentGoldPrice(referencePeriod, currency) { price =>
			println("The recent average price of gold is:")
			WeightUnit.values.foreach { unit =>
				println(s"\t- ${price per unit} $currency/$unit")
			}
		}
	}
	
	/**
	 * Prints and exports gold prices as a csv file
	 * @param dates Targeted dates
	 * @param targetPath Path to the file to write (call-by-name)
	 * @param currency Used currency (implicit)
	 * @param connection Implicit DB connection
	 */
	def exportGoldPricesDuring(dates: DateRange, targetPath: => Path)
	                          (implicit connection: Connection, currency: Currency) =
	{
		forApiKeyOrCancel { implicit key =>
			println(s"Acquires and displays gold prices in $currency during $dates")
			MetalPrices(Gold, currency).during(dates).waitForResult() match {
				case TryCatch.Success(prices: Map[LocalDate, WeightPrice], errors) =>
					if (errors.nonEmpty) {
						log(errors.head, "Partial failures during price-search")
						println(s"Encountered ${ errors.size } errors during the price-search")
					}
					
					// Prints an ASCII table and generates a csv file
					val orderedPrices = prices.toVector.sortBy { _._1 }
					println(StringUtils.asciiTableFrom[(LocalDate, WeightPrice)](orderedPrices,
						Vector("Date", s"$currency/Kg", s"$currency/g", s"$currency/t oz"),
						_._1.toString,
						p => f"${ p._2.perKilo }%1.2f", p => f"${ p._2.perGram }%1.2f", p => f"${ p._2.perTroyOunce }%1.2f"))
					
					targetPath.createParentDirectories()
						.flatMap { p =>
							p.writeUsing { writer =>
								writer.println(s"Date; $currency/Kg; $currency/g; $currency/t oz")
								orderedPrices.foreach { case (date, price) =>
									val perKilo = f"${ price.perKilo }%1.2f"
									val perGram = f"${ price.perGram }%1.2f"
									val perTroyOunce = f"${ price.perTroyOunce }%1.2f"
									writer.println(s"$date;$perKilo;$perGram;$perTroyOunce")
								}
								p
							}
						}
						.flatMap { _.openFileLocation() }
						.failure.foreach { error => log(error, "Failed to write or open the price file") }
				
				case TryCatch.Failure(error) =>
					log(error, "Failed to acquire prices")
					println(s"Failed to acquire gold prices (${error.getMessage})")
			}
		}
	}
	
	/**
	 * Displays the current gold value of a specific euro amount.
	 * Uses the recent average value of gold.
	 * @param amount Amount of fiat currency measured
	 * @param referencePeriod Duration for the average gold price calculation. Default = 30 days.
	 * @param connection Implicit DB Connection
	 */
	def currentGoldValueOf(amount: Price, referencePeriod: Days = defaultReferencePeriod)
	                      (implicit connection: Connection) =
		forCurrentGoldPrice(referencePeriod, amount.currency) { price =>
			val acquiredWeight = price.weightForMoney(amount.amount)
			println(s"Amount of gold that may currently be purchased with $amount is:")
			WeightUnit.values.foreach { unit =>
				println(s"\t- ${acquiredWeight in unit} $unit")
			}
		}
	
	/**
	 * Determines the current value of a previously agreed euro sum
	 * @param originalPrice Previously agreed sum
	 * @param originalDate Date when that sum was agreed
	 * @param referencePeriod Duration for determining the average metal price (default = 30 days)
	 * @param includeSilver Whether to account for silver price changes in addition to gold price changes.
	 *                      Default = false.
	 */
	def determineCurrentPrice(originalPrice: Price, originalDate: LocalDate,
	                          referencePeriod: Days = defaultReferencePeriod, includeSilver: Boolean = false): Unit =
	{
		forApiKeyOrCancel { implicit apiKey: ApiKey =>
			println(s"Determining the current price of $originalDate's $originalPrice...")
			CorrectInflation(originalPrice, originalDate,
				if (includeSilver) Set(Gold, Silver) else Set(Gold), referencePeriod)
				.waitForResult() match
			{
				case TryCatch.Success(price, failures) =>
					failures.headOption.foreach { error =>
						error.printStackTrace()
						println(s"Some of the price requests failed (${failures.size} failures in total)")
					}
					println("\nResult:")
					println(s"$originalDate's $originalPrice would now be worth ${price.current}")
					price.metalValues.foreachEntry { (metal, weight) =>
						println(s"Original value in $metal")
						WeightUnit.values.foreach { unit =>
							println(s"\t- ${weight in unit} $unit")
						}
					}
					println(s"Value of ${ originalPrice.currency } has suffered ${
						(price.inflation * 100).round}% inflation since ${price.originalDate}")
				case TryCatch.Failure(error) =>
					error.printStackTrace()
					println("Failed to determine price changes. See the error above.")
			}
		}
	}
	
	private def forCurrentGoldPrice[U](referencePeriod: Days, currency: Currency)(f: WeightPrice => U)
	                                  (implicit connection: Connection) =
	{
		// Access to the API is required
		forApiKeyOrCancel { implicit key: ApiKey =>
			println(s"Checking gold prices for the last ${referencePeriod.length} days")
			val lastDate = Today.yesterday
			// Retrieves the recent average price (blocks)
			MetalPrices(Gold, currency)
				.averageDuring(DateRange.inclusive(lastDate - referencePeriod, lastDate))
				.waitForResult() match
			{
				// Case: Price acquired => Prints possible warnings and then delegates to the specified function
				case TryCatch.Success(price, errors) =>
					println()
					errors.headOption.foreach { error =>
						error.printStackTrace()
						println(s"WARNING: Encountered ${
							errors.size
						} failures while checking gold prices. \nResults might not be fully accurate. \nPlease see the error above.")
					}
					f(price)
				// Case: Price was not acquired => Informs the user
				case TryCatch.Failure(error) =>
					error.printStackTrace()
					println(s"Failed to check the current average gold price (${
						error.getMessage
					}). \nSee the error above.")
			}
		}
	}
	
	// Calls the specified function for a valid API-key.
	// Informs the user about process cancellation if API-key can't be acquired.
	private def forApiKeyOrCancel[U](f: ApiKey => U): Unit = {
		apiKeyCache.value match {
			case Success(key) =>
				key match {
					case Some(key) => f(key)
					case None => println("No API-key specified. Cancels.")
				}
			case Failure(error) =>
				error.printStackTrace()
				println("Failed to access the metal price API-key. Can't proceed.")
		}
	}
}
