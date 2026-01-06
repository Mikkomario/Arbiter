package vf.arbiter.gold.controller.price

import utopia.flow.async.AsyncExtensions._
import utopia.flow.async.TryFuture
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Empty
import utopia.flow.time.{DateRange, Days, Today}
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.result.TryCatch
import utopia.flow.util.result.TryExtensions._
import utopia.vault.database.{Connection, ConnectionPool}
import vf.arbiter.gold.database.access.many.price.DbMetalPrices
import vf.arbiter.gold.database.model.price.MetalPriceModel
import vf.arbiter.gold.model.cached.auth.ApiKey
import vf.arbiter.gold.model.cached.price.WeightPrice
import vf.arbiter.gold.model.enumeration.{Currency, Metal}

import java.time.LocalDate
import scala.collection.immutable.VectorBuilder
import scala.concurrent.{ExecutionContext, Future}

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
	 * @param dates Targeted date range
	 * @param cPool Implicit DB connection pool (used when asynchronously caching data)
	 * @param exc Implicit execution context
	 * @param apiKey Implicit API key
	 * @param connection Implicit DB connection (used in the initial cache search)
	 * @return Future that resolves into a map where keys are dates and values are average metal prices for that date.
	 *         May contain a full or a partial failure.
	 */
	def during(dates: DateRange)
	          (implicit cPool: ConnectionPool, exc: ExecutionContext, apiKey: ApiKey, connection: Connection) =
	{
		// Checks the cached prices first
		val cachedPrices = access.during(dates).pull.view.map { p => p.date -> p.price }.toMap
		
		// Finds the first and the last date not covered by the cached price data
		dates.find { !cachedPrices.contains(_) } match {
			// Case: No local data exists for some dates => Queries the missing data
			case Some(firstMissingDate) =>
				val lastMissingDate = dates.reverse.iterator.find { !cachedPrices.contains(_) }.get
				Future { pullDuring(DateRange.inclusive(firstMissingDate, lastMissingDate), cachedPrices) }
				
			// Case: Cached data covers the whole range of targeted dates => Calculates average based on those
			case None => TryFuture.successCatching(cachedPrices)
		}
	}
	
	/**
	 * @param dates Targeted dates
	 * @param cPool Implicit connection pool
	 * @param exc Implicit execution context
	 * @param apiKey Implicit API-key used when accessing the metal price API
	 * @param connection Implicit connection used for pulling the initial cached data
	 * @return A future that resolves into the average price in this metal + currency pair during the targeted dates.
	 *         Contains a full failure if no data could be read.
	 *         Contains a partial failure if some of the data could be read, but the whole of the date range
	 *         was not covered.
	 */
	def averageDuring(dates: DateRange)
	                 (implicit cPool: ConnectionPool, exc: ExecutionContext, apiKey: ApiKey,
	                  connection: Connection): Future[TryCatch[WeightPrice]] =
		during(dates).map { _.map { prices => averageOf(prices.values) } }
	
	private def pullDuring(targetDates: DateRange, cachedPrices: Map[LocalDate, WeightPrice])
	                      (implicit cPool: ConnectionPool, exc: ExecutionContext, apiKey: ApiKey) =
	{
		// Splits the targeted time period so that it respects the API's max request sizes
		// Performs sequential requests for each of the targeted segments, but stops if any query fails
		val (splitTargetDates, extraDates) = splitRequest(targetDates, cachedPrices.keySet,
			Days(if (apiKey.paid) 365 else 5))
		
		val result = splitTargetDates.reverseIterator
			.map { dates =>
				println(s"Requesting prices for $dates")
				MetalPriceApiClient.pricesDuring(metal, currency, DateRange.inclusive(dates.head, dates.last))
					.waitForResult()
			}
			.takeTo { _.isFailure }
			.toTryCatch
			.map { datePrices =>
				// On a full or partial success, calculates the average price and caches all prices in the local DB
				val newPriceData = datePrices.view.flatten.filterNot { p => cachedPrices.contains(p.date) }.toVector
				if (newPriceData.nonEmpty)
					cPool.tryWith { implicit c =>
						// TODO: Doesn't check for duplicates.
						//  In a very busy environment there is a risk for those
						MetalPriceModel.insert(newPriceData)
					}
				
				newPriceData.view.filterNot { p => extraDates.contains(p.date) }.map { p => p.date -> p.price }.toMap ++
					cachedPrices
			}
		
		// If the process failed, attempts to recover using cached data
		if (result.isSuccess || cachedPrices.isEmpty)
			result
		else
			TryCatch.Success(cachedPrices, result.failures)
	}
	
	/**
	 * Splits a date range into sizes supported by the metal price API.
	 * Optimizes request-usage by extending the first and/or last date range (so that more data may be cached).
	 * @param dates Targeted dates
	 * @param cachedDates Dates for which there already exists cached data
	 * @param maxRequestLength Maximum number of days in a single request
	 * @return Date ranges to query,
	 *         as well as a set containing all dates that were included but not part of the targeted date range.
	 */
	private def splitRequest(dates: DateRange, cachedDates: Set[LocalDate], maxRequestLength: Days) = {
		val maxAdvance = maxRequestLength - 1
		val iter = dates.iterator.filterNot(cachedDates.contains).pollable
		
		if (iter.hasNext) {
			val rangesBuilder = new VectorBuilder[DateRange]()
			var openRangeStart = iter.next()
			var openRangeEnd = openRangeStart
			
			while (iter.hasNext) {
				val date = iter.next()
				if (date > openRangeStart + maxAdvance) {
					rangesBuilder += DateRange.inclusive(openRangeStart, openRangeEnd)
					openRangeStart = date
				}
				openRangeEnd = date
			}
			
			// Adds additional days to the latest request if there is additional space
			// Can't target current or future dates, however
			val extraDaysAtEnd = {
				val yesterday = Today.yesterday
				if (openRangeEnd < yesterday && openRangeEnd - openRangeStart < maxAdvance) {
					val lastDate = (openRangeStart + maxAdvance) min yesterday
					val previousLastDate = openRangeEnd
					openRangeEnd = lastDate
					Some(DateRange.inclusive(previousLastDate.tomorrow, lastDate))
				}
				else
					None
			}
			rangesBuilder += DateRange.inclusive(openRangeStart, openRangeEnd)
			val defaultRanges = rangesBuilder.result()
			
			// May also add additional days the earliest request, in order to optimize request-usage
			val (firstRange, extraDaysAtStart) = {
				val firstRange = defaultRanges.head
				val extraDaysUsed = extraDaysAtEnd match {
					case Some(days) => days.length
					case None => Days.zero
				}
				val remainingDays = maxRequestLength - firstRange.length - extraDaysUsed
				if (remainingDays.isPositive) {
					val newFirstDate = firstRange.start - remainingDays
					firstRange.withStart(newFirstDate) -> Some(DateRange.exclusive(newFirstDate, firstRange.start))
				}
				else
					firstRange -> None
			}
			
			(firstRange +: defaultRanges.tail) ->
				(Set.concat(extraDaysAtEnd.view.flatten, extraDaysAtStart.view.flatten) -- cachedDates)
		}
		else
			Empty -> Set[LocalDate]()
	}
	
	private def averageOf(prices: Iterable[WeightPrice]) = prices.sum / prices.size
}
