package vf.arbiter.command.app

import utopia.flow.async.AsyncExtensions._
import utopia.flow.time.TimeExtensions._
import utopia.flow.time.{DateRange, Days, Today}
import utopia.flow.util.TryCatch
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.view.immutable.caching.ConditionalLazy
import utopia.vault.database.Connection
import vf.arbiter.core.util.Common._
import vf.arbiter.gold.controller.price.{CorrectInflation, MetalPrices}
import vf.arbiter.gold.controller.settings.ArbiterGoldSettings
import vf.arbiter.gold.model.cached.auth.ApiKey
import vf.arbiter.gold.model.cached.price.WeightPrice
import vf.arbiter.gold.model.enumeration.Currency.Euro
import vf.arbiter.gold.model.enumeration.Metal.{Gold, Silver}
import vf.arbiter.gold.model.enumeration.WeightUnit

import java.time.LocalDate
import scala.io.StdIn
import scala.util.{Failure, Success}

/**
 * Provides interactive methods for gold-related functions
 * @author Mikko Hilpinen
 * @since 15.9.2023, v1.4
 */
object GoldActions
{
	// ATTRIBUTES   --------------------------------
	
	private val defaultReferencePeriod = Days(30)
	
	private val apiKeyCache = ConditionalLazy {
		val result = connectionPool.tryWith { implicit c =>
			// Reads the key from DB if possible
			ArbiterGoldSettings.apiKey.orElse {
				// If not stored in the DB, requests the user for a new key
				val key = StdIn.readNonEmptyLine(
					"Please specify the API-key for the metal prices API.\nIf you don't have an API-key yet, visit https://metalpriceapi.com/ to setup a free account.")
				key.foreach { ArbiterGoldSettings.apiKey = _ }
				key.map(ApiKey.apply)
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
	def printCurrentGoldPrice(referencePeriod: Days = defaultReferencePeriod)(implicit connection: Connection) = {
		forCurrentGoldPrice(referencePeriod) { price =>
			println("The recent average price of gold is:")
			WeightUnit.values.foreach { unit =>
				println(s"\t- ${price per unit} €/$unit")
			}
		}
	}
	
	/**
	 * Displays the current gold value of a specific euro amount.
	 * Uses the recent average value of gold.
	 * @param euroAmount Amount of Euros measured
	 * @param referencePeriod Duration for the average gold price calculation. Default = 30 days.
	 * @param connection Implicit DB Connection
	 */
	def currentGoldValueOfEuros(euroAmount: Double, referencePeriod: Days = defaultReferencePeriod)
	                           (implicit connection: Connection) =
		forCurrentGoldPrice(referencePeriod) { price =>
			val acquiredWeight = price.weightForMoney(euroAmount)
			println(s"Amount of gold that may currently be purchased with $euroAmount € is:")
			WeightUnit.values.foreach { unit =>
				println(s"\t- ${acquiredWeight in unit} $unit")
			}
		}
	
	/**
	 * Determines the current value of a previously agreed euro sum
	 * @param originalEuros Previously agreed sum
	 * @param originalDate Date when that sum was agreed
	 * @param referencePeriod Duration for determining the average metal price (default = 30 days)
	 * @param includeSilver Whether to account for silver price changes in addition to gold price changes.
	 *                      Default = false.
	 */
	def determineCurrentPrice(originalEuros: Double, originalDate: LocalDate,
	                          referencePeriod: Days = defaultReferencePeriod, includeSilver: Boolean = false): Unit =
	{
		forApiKeyOrCancel { implicit apiKey: ApiKey =>
			println(s"Determining the current price of $originalDate's $originalEuros €...")
			CorrectInflation(originalEuros, Euro, originalDate,
				if (includeSilver) Set(Gold, Silver) else Set(Gold), referencePeriod)
				.waitForResult() match {
				case TryCatch.Success(price, failures) =>
					failures.headOption.foreach { error =>
						error.printStackTrace()
						println(s"Some of the price requests failed (${failures.size} failures in total)")
					}
					println("\nResult:")
					println(s"$originalDate's $originalEuros € would now be worth ${price.currentPrice} €")
					price.metalValues.foreachEntry { (metal, weight) =>
						println(s"Original value in $metal")
						WeightUnit.values.foreach { unit =>
							println(s"\t- ${weight in unit} $unit")
						}
					}
					println(s"Value of Euro has suffered ${(price.inflation * 100).round}% inflation since ${
						price.originalDate
					}")
				case TryCatch.Failure(error) =>
					error.printStackTrace()
					println("Failed to determine price changes. See the error above.")
			}
		}
	}
	
	private def forCurrentGoldPrice[U](referencePeriod: Days)(f: WeightPrice => U)(implicit connection: Connection) =
	{
		// Access to the API is required
		forApiKeyOrCancel { implicit key: ApiKey =>
			println(s"Checking gold prices for the last ${referencePeriod.length} days")
			val lastDate = Today.yesterday
			// Retrieves the recent average price (blocks)
			MetalPrices(Gold, Euro).averageDuring(DateRange.inclusive(lastDate - referencePeriod, lastDate))
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
