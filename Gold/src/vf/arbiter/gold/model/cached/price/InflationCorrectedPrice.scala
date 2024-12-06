package vf.arbiter.gold.model.cached.price

import vf.arbiter.gold.model.enumeration.{Currency, Metal}

import java.time.LocalDate

object InflationCorrectedPrice
{
	/**
	 * Represents a price that has not been corrected against inflation (assumes inflation of 0%)
	 * @param originalPrice A monetary value
	 * @param originalDate Date when that value was valid
	 * @return A new dataset that doesn't count for inflation
	 */
	def notCorrected(originalPrice: Price, originalDate: LocalDate) =
		apply(originalPrice.amount, originalDate, Map(), originalPrice.amount, originalPrice.currency)
}

/**
 * Represents a price that was set in an inflammatory fiat currency,
 * which has now been corrected against current date's monetary supply by comparing
 * the average metal prices then and now.
 * @author Mikko Hilpinen
 * @since 15.9.2023, v1.4
 *
 * @constructor Constructs a new inflation-correction dataset
 * @param originalAmount Original value in that date's fiat currency
 * @param originalDate Date when 'originalPrice' was valid
 * @param metalValues The amount of metal that could have been purchased with the original price on the original date
 *                    (may be using the average metal price for the previous X days)
 * @param currentAmount Today's value of that same amount of metal
 *                     (again, may be using average metal prices)
 * @param currency The (fiat) currency in which the original and current prices are given
 */
case class InflationCorrectedPrice(originalAmount: Double, originalDate: LocalDate, metalValues: Map[Metal, Weight],
                                   currentAmount: Double, currency: Currency)
{
	/**
	 * The original price
	 */
	lazy val original = Price(originalAmount, currency)
	/**
	 * The current inflation-corrected price
	 */
	lazy val current = Price(currentAmount, currency)
	
	/**
	 * @return The rate of inflation suffered by the specified currency since the original date.
	 *         0.0 means no inflation, 0.1 would mean 10% inflation (i.e. 10% loss in purchasing power),
	 *         -0.1 would mean 10% deflation (i.e. 10% increase in purchasing power)
	 */
	def inflation = 1 - originalAmount / currentAmount
}
