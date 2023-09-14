package vf.arbiter.gold.database.access.many.price

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.time.DateRange
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.arbiter.gold.database.factory.price.MetalPriceFactory
import vf.arbiter.gold.database.model.price.MetalPriceModel
import vf.arbiter.gold.model.enumeration.{Currency, Metal}
import vf.arbiter.gold.model.stored.price.MetalPrice

import java.time.LocalDate

object ManyMetalPricesAccess
{
	// NESTED	--------------------
	
	private class ManyMetalPricesSubView(condition: Condition) extends ManyMetalPricesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple metal prices at a time
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
trait ManyMetalPricesAccess 
	extends ManyRowModelAccess[MetalPrice] with FilterableView[ManyMetalPricesAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * metals of the accessible metal prices
	  */
	def metals(implicit connection: Connection) = 
		pullColumn(model.metalColumn).map { v => v.getInt }.flatMap(Metal.findForId)
	/**
	  * currencies of the accessible metal prices
	  */
	def currencies(implicit connection: Connection) = 
		pullColumn(model.currencyColumn).map { v => v.getInt }.flatMap(Currency.findForId)
	/**
	  * dates of the accessible metal prices
	  */
	def dates(implicit connection: Connection) =
		pullColumn(model.dateColumn).map { v => v.getLocalDate }
	/**
	  * price per troy ounces of the accessible metal prices
	  */
	def pricePerTroyOunces(implicit connection: Connection) = 
		pullColumn(model.pricePerTroyOunceColumn).map { v => v.getDouble }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MetalPriceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MetalPriceFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyMetalPricesAccess = 
		new ManyMetalPricesAccess.ManyMetalPricesSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	 * @param metal Targeted metal
	 * @return Access to prices of that metal only
	 */
	def of(metal: Metal) = filter(model.withMetal(metal).toCondition)
	/**
	 * @param currency Targeted currency
	 * @return Access to price entries listed in that currency
	 */
	def in(currency: Currency) = filter(model.withCurrency(currency).toCondition)
	
	/**
	 * @param date Targeted date
	 * @return Access to metal prices during that date
	 */
	def during(date: LocalDate) = filter(model.withDate(date).toCondition)
	/**
	 * @param dates Targeted dates
	 * @return Access to metal prices during those dates
	 */
	def during(dates: DateRange): ManyMetalPricesAccess = {
		// Case: Targets a single date only
		if (dates.isSingleDate)
			during(dates.start)
		// Case: Targets no dates
		else if (dates.isEmpty)
			filter(Condition.alwaysFalse)
		// Case: Targets multiple dates
		else
			filter(model.dateColumn.isBetween(dates.start, dates.last))
	}
	
	/**
	  * Updates the currencies of the targeted metal prices
	  * @param newCurrency A new currency to assign
	  * @return Whether any metal price was affected
	  */
	def currencies_=(newCurrency: Currency)(implicit connection: Connection) = 
		putColumn(model.currencyColumn, newCurrency.id)
	/**
	  * Updates the dates of the targeted metal prices
	  * @param newDate A new date to assign
	  * @return Whether any metal price was affected
	  */
	def dates_=(newDate: LocalDate)(implicit connection: Connection) = putColumn(model.dateColumn, newDate)
	/**
	  * Updates the metals of the targeted metal prices
	  * @param newMetal A new metal to assign
	  * @return Whether any metal price was affected
	  */
	def metals_=(newMetal: Metal)(implicit connection: Connection) = putColumn(model.metalColumn, newMetal.id)
	/**
	  * Updates the price per troy ounces of the targeted metal prices
	  * @param newPricePerTroyOunce A new price per troy ounce to assign
	  * @return Whether any metal price was affected
	  */
	def pricePerTroyOunces_=(newPricePerTroyOunce: Double)(implicit connection: Connection) = 
		putColumn(model.pricePerTroyOunceColumn, newPricePerTroyOunce)
}

