package vf.arbiter.gold.controller.price

import utopia.flow.async.AsyncExtensions._
import utopia.flow.async.TryFuture
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Pair
import utopia.flow.time.TimeExtensions._
import utopia.flow.time.{DateRange, Days, Today}
import utopia.flow.util.TryCatch
import utopia.vault.database.ConnectionPool
import vf.arbiter.gold.model.cached.auth.ApiKey
import vf.arbiter.gold.model.cached.price.InflationCorrectedPrice
import vf.arbiter.gold.model.enumeration.{Currency, Metal}

import java.time.LocalDate
import scala.collection.immutable.VectorBuilder
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * An interface for performing inflation-correcting measures by comparing monetary currency prices to the prices of
 * valuable metals.
 * @author Mikko Hilpinen
 * @since 15.9.2023, v1.4
 */
object CorrectInflation
{
	/**
	 * Determines the current date inflation-corrected value of a previous monetary amount
	 * @param originalPrice Price on 'contractDate'
	 * @param currency Applicable currency
	 * @param contractDate Date on which the 'originalPrice' was set / agreed
	 * @param metals Metals used in inflation-correcting (if empty, inflation won't be corrected)
	 * @param referenceDuration Duration for calculating the average (i.e. stable) metal price.
	 *                          E.g. If set to 7 days, the average price during the last 7 days
	 *                          before 'contractDate' and today will be used.
	 * @param cPool Implicit connection pool to use
	 * @param exc Implicit execution context to use
	 * @return Asynchronous completion of the inflation-correction process,
	 *         yielding the inflation-corrected monetary amount, if successful.
	 *         May contain partial failure information, in case only partial data was available for the
	 *         inflation calculation process.
	 */
	def apply(originalPrice: Double, currency: Currency, contractDate: LocalDate, metals: Set[Metal],
	          referenceDuration: Days)
	         (implicit cPool: ConnectionPool, exc: ExecutionContext, apiKey: ApiKey) =
	{
		if (contractDate.isFuture)
			throw new IllegalArgumentException("Can't correct inflation from the future")
		if (referenceDuration <= Days.zero)
			throw new IllegalArgumentException("Reference duration must be positive")
		
		// Case: No metals are used for checking inflation, or no time has passed since the contract was formed =>
		// No need to check for metal prices
		if (metals.isEmpty || contractDate.isToday)
			TryFuture.successCatching(InflationCorrectedPrice.notCorrected(originalPrice, contractDate, currency))
		else {
			// TODO: Optimize by potentially pulling both dates simultaneously, if close together
			// Retrieves average price data for both the contract date and the current date
			// (contract date and today not included)
			val dateRanges = Pair(contractDate, Today.toLocalDate)
				.map { d => DateRange.exclusive(d - (referenceDuration + 1), d) }
			Future {
				cPool.tryWith { implicit c =>
					// Retrieves all required prices one by one in order to avoid duplicate queries
					metals.toVector.map { metal =>
						metal -> dateRanges.map { dates => metal.pricesIn(currency).averageDuring(dates).waitForResult() }
					}
				}.flatMapCatching { results =>
					val partialFailuresBuilder = new VectorBuilder[Throwable]()
					// Handles possible failure cases
					val (failures, metalPrices) = results.map { case (metal, prices) =>
						// Checks whether price check failed for either side
						prices.findMap { _.failure } match {
							// Case: Price check failed for one or both sides => Can't determine the rate of inflation
							case Some(failure) => Failure(failure)
							// Case: Price check succeeded at least partially for both sides =>
							// Determines the rate of inflation
							case None =>
								// The average metal prices then and now
								val averageMetalPrices = prices.map { p =>
									// Remembers partial failures
									partialFailuresBuilder ++= p.partialFailures
									p.get
								}
								Success(metal -> averageMetalPrices)
								/*
								val suggestedPrice = prices
									.map { p =>
										// Remembers partial failures
										partialFailuresBuilder ++= p.partialFailures
										p.get
									}
									// Calculates the price of the original agreement, according to the rate of inflation
									.merge { (priceThen, priceNow) => originalPrice * (priceNow / priceThen) }
								Success(suggestedPrice)
								 */
						}
					}.divided
					// Case: No prices could be acquired for any metal => Fails
					if (metalPrices.isEmpty)
						TryCatch.Failure(failures.headOption.getOrElse { partialFailuresBuilder.result().head })
					// Case: Prices could be acquired =>
					// Determines the final price and returns with encountered non-critical failures
					else {
						// (type of metal, original value in weight, current value in fiat)
						val data = metalPrices.map { case (metal, prices) =>
							// Amount of correction that needs to be applied to the original fiat value
							// in order to reflect that same value today
							val correctionMod = prices.second / prices.first
							val weightThen = prices.first.weightForMoney(originalPrice)
							(metal, weightThen, originalPrice * correctionMod)
						}
						val corrected = InflationCorrectedPrice(originalPrice, contractDate,
							data.map { case (metal, wt, _) => metal -> wt }.toMap, data.map { _._3 }.sum / data.size,
							currency)
						TryCatch.Success(corrected, failures ++ partialFailuresBuilder.result())
					}
				}
			}
		}
	}
}
