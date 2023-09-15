package vf.arbiter.gold.controller.price

import utopia.flow.async.AsyncExtensions._
import utopia.flow.async.TryFuture
import utopia.flow.time.DateRange
import utopia.flow.util.TryCatch
import utopia.vault.database.ConnectionPool
import vf.arbiter.gold.database.access.many.price.DbMetalPrices
import vf.arbiter.gold.database.model.price.MetalPriceModel
import vf.arbiter.gold.model.cached.price.WeightPrice
import vf.arbiter.gold.model.enumeration.{Currency, Metal}

import scala.concurrent.ExecutionContext
import scala.util.Success

object MetalPrices
{
	/**
	 * @param metal Targeted type of metal
	 * @param currency Targeted type of currency
	 * @return Access to price data concerning the targeted metal in the targeted currency
	 */
	def apply(metal: Metal, currency: Currency) = new MetalPrices(metal, currency)
}

/**
 * An interface for accessing the values of precious metals
 * @author Mikko Hilpinen
 * @since 14.9.2023, v1.4
 */
class MetalPrices(metal: Metal, currency: Currency)
{
	// ATTRIBUTES   -----------------------
	
	private lazy val access = DbMetalPrices.of(metal).in(currency)
	
	
	// OTHER    ---------------------------
	
	/**
	 * @param dates Targeted dates
	 * @param cPool Implicit connection pool
	 * @param exc Implicit execution context
	 * @return A future that resolves into the average price in this metal + currency pair during the targeted dates.
	 *         Contains a full failure if no data could be read.
	 *         Contains a partial failure if some of the data could be read, but the whole of the date range
	 *         was not covered.
	 */
	def averageDuring(dates: DateRange)(implicit cPool: ConnectionPool, exc: ExecutionContext) = {
		// Checks the cached prices first
		cPool.tryWith { implicit c => access.during(dates).pull.map { p => p.date -> p.price }.toMap }
			.flatMap { cachedPrices =>
				// Finds the first and the last date not covered by the cached price data
				dates.iterator.find { !cachedPrices.contains(_) } match {
					case Some(firstMissingDate) =>
						val lastMissingDate = dates.reverse.iterator.find { !cachedPrices.contains(_) }.get
						// Requests price data for the missing dates
						MetalPriceApi.initialized.map { api =>
							api.pricesDuring(metal, currency, DateRange.inclusive(firstMissingDate, lastMissingDate))
								.map {
									// Case: Price request succeeded => Stores the read prices and produces the average
									case TryCatch.Success(priceData, errors) =>
										val newPriceData = priceData.filterNot { p => cachedPrices.contains(p.date) }
										if (newPriceData.nonEmpty)
											cPool.tryWith { implicit c =>
												// TODO: Doesn't check for duplicates.
												//  In a very busy environment there is a risk for those
												MetalPriceModel.insert(newPriceData)
											}
										TryCatch.Success(
											averageOf(newPriceData.map { _.price } ++ cachedPrices.valuesIterator),
											errors)
										
									// Case: Request failed => Recovers using cached data, if possible
									case TryCatch.Failure(error) =>
										if (cachedPrices.isEmpty)
											TryCatch.Failure(error)
										else
											TryCatch.Success(averageOf(cachedPrices.values), Vector(error))
								}
						}
					// Case: Cached data covers the whole range of targeted dates => Calculates average based on those
					case None => Success(TryFuture.successCatching(averageOf(cachedPrices.values)))
				}
			}.flattenToFuture
	}
	
	private def averageOf(prices: Iterable[WeightPrice]) = prices.sum / prices.size
}
