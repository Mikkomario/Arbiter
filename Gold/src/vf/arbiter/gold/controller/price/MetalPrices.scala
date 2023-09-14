package vf.arbiter.gold.controller.price

import utopia.flow.time.DateRange
import utopia.flow.util.TryCatch
import utopia.vault.database.ConnectionPool
import vf.arbiter.gold.database.access.many.price.DbMetalPrices
import vf.arbiter.gold.database.model.price.MetalPriceModel
import vf.arbiter.gold.model.enumeration.{Currency, Metal}
import vf.arbiter.gold.model.partial.price.MetalPriceData

import scala.concurrent.ExecutionContext

/**
 * An interface for accessing the values of precious metals
 * @author Mikko Hilpinen
 * @since 14.9.2023, v1.4
 */
class MetalPrices(metal: Metal, currency: Currency)
{
	lazy val access = DbMetalPrices.of(metal).in(currency)
	
	// TODO: Add support for partial successes
	def averageDuring(dates: DateRange)(implicit cPool: ConnectionPool, exc: ExecutionContext) = {
		// Checks the cached prices first
		cPool.tryWith { implicit c => access.during(dates).pull.map { p => p.date -> p.pricePerTroyOunce }.toMap }
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
										
									// Case: Request failed
									case TryCatch.Failure(error) =>
								}
						}
					case None => ???
				}
			}
	}
	
	private def averageOf(prices: Iterable[MetalPriceData]) = {
		???
	}
}
