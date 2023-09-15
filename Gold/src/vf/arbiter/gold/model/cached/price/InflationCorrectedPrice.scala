package vf.arbiter.gold.model.cached.price

import vf.arbiter.gold.model.enumeration.{Currency, Metal}

import java.time.LocalDate

object InflationCorrectedPrice
{
	/**
	 * Represents a price that has not been corrected against inflation (assumes inflation of 0%)
	 * @param originalPrice A monetary value in the specified currency
	 * @param originalDate Date when that value was valid
	 * @param currency Currency in which 'originalPrice' is given
	 * @return A new dataset that doesn't count for inflation
	 */
	def notCorrected(originalPrice: Double, originalDate: LocalDate, currency: Currency) =
		apply(originalPrice, originalDate, Map(), originalPrice, currency)
}

/**
 * Represents a price that was set in an inflammatory fiat currency,
 * which has now been corrected against current date's monetary supply by comparing
 * the average metal prices then and now.
 * @author Mikko Hilpinen
 * @since 15.9.2023, v1.4
 *
 * @constructor Constructs a new inflation-correction dataset
 * @param originalPrice Original value in that date's fiat currency
 * @param originalDate Date when 'originalPrice' was valid
 * @param metalValues The amount of metal that could have been purchased with the original price on the original date
 *                    (may be using the average metal price for the previous X days)
 * @param currentPrice Today's value of that same amount of metal
 *                     (again, may be using average metal prices)
 * @param currency The (fiat) currency in which the original and current prices are given
 */
case class InflationCorrectedPrice(originalPrice: Double, originalDate: LocalDate, metalValues: Map[Metal, Weight],
                                   currentPrice: Double, currency: Currency)
{
	/**
	 * @return The rate of inflation suffered by the specified currency since the original date.
	 *         0.0 means no inflation, 0.1 would mean 10% inflation (i.e. 10% loss in purchasing power),
	 *         -0.1 would mean 10% deflation (i.e. 10% increase in purchasing power)
	 */
	def inflation = 1 - originalPrice / currentPrice
}
