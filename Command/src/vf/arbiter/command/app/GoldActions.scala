package vf.arbiter.command.app

import utopia.flow.async.AsyncExtensions._
import utopia.flow.time.Days
import utopia.flow.util.TryCatch
import utopia.flow.view.immutable.caching.ConditionalLazy
import utopia.flow.util.console.ConsoleExtensions._
import vf.arbiter.core.util.Common._
import vf.arbiter.gold.controller.price.CorrectInflation
import vf.arbiter.gold.controller.settings.ArbiterGoldSettings
import vf.arbiter.gold.model.cached.auth.ApiKey
import vf.arbiter.gold.model.enumeration.Currency.Euro
import vf.arbiter.gold.model.enumeration.Metal.{Gold, Silver}

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
	 * Determines the current value of a previously agreed euro sum
	 * @param originalEuros Previously agreed sum
	 * @param originalDate Date when that sum was agreed
	 * @param referencePeriod Duration for determining the average metal price (default = 30 days)
	 * @param includeSilver Whether to account for silver price changes in addition to gold price changes.
	 *                      Default = false.
	 */
	def determineCurrentPrice(originalEuros: Double, originalDate: LocalDate, referencePeriod: Days = Days(30),
	                          includeSilver: Boolean = false) = {
		apiKeyCache.value match {
			case Success(key) =>
				key match {
					case Some(key) =>
						implicit val apiKey: ApiKey = key
						println(s"Determining the current price of $originalDate's $originalEuros €...")
						CorrectInflation(originalEuros, Euro, originalDate,
							if (includeSilver) Set(Gold, Silver) else Set(Gold), referencePeriod)
							.waitForResult() match {
								case TryCatch.Success(price, failures) =>
									failures.headOption.foreach { error =>
										error.printStackTrace()
										println(s"Some of the price requests failed (${failures.size} failures in total)")
									}
									println(s"$originalDate's $originalEuros € is now worth $price €")
								case TryCatch.Failure(error) =>
									error.printStackTrace()
									println("Failed to determine price changes. See the error above.")
							}
					case None => println("No API-key specified. Cancels the price-check process.")
				}
			case Failure(error) =>
				error.printStackTrace()
				println("Failed to access the metal price API-key. Can't determine price-changes")
		}
	}
}
