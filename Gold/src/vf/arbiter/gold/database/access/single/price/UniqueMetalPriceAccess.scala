package vf.arbiter.gold.database.access.single.price

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.arbiter.gold.database.factory.price.MetalPriceFactory
import vf.arbiter.gold.database.model.price.MetalPriceModel
import vf.arbiter.gold.model.enumeration.{Currency, Metal}
import vf.arbiter.gold.model.stored.price.MetalPrice

import java.time.LocalDate

object UniqueMetalPriceAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueMetalPriceAccess = new _UniqueMetalPriceAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueMetalPriceAccess(condition: Condition) extends UniqueMetalPriceAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct metal prices.
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
trait UniqueMetalPriceAccess 
	extends SingleRowModelAccess[MetalPrice] with FilterableView[UniqueMetalPriceAccess] 
		with DistinctModelAccess[MetalPrice, Option[MetalPrice], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Metal who's price is recorded. None if no metal price (or value) was found.
	  */
	def metal(implicit connection: Connection) = pullColumn(model.metalColumn).int.flatMap(Metal.findForId)
	
	/**
	  * The currency in which the price is given. None if no metal price (or value) was found.
	  */
	def currency(implicit connection: Connection) = 
		pullColumn(model.currencyColumn).int.flatMap(Currency.findForId)
	
	/**
	  * Date on which the price was used. None if no metal price (or value) was found.
	  */
	def date(implicit connection: Connection) = pullColumn(model.dateColumn).localDate
	
	/**
	  * Price of the specified metal in the specified currency. Per one troy ounce of metal.. None if
	  *  no metal price (or value) was found.
	  */
	def pricePerTroyOunce(implicit connection: Connection) = pullColumn(model.pricePerTroyOunceColumn).double
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MetalPriceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MetalPriceFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueMetalPriceAccess = 
		new UniqueMetalPriceAccess._UniqueMetalPriceAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the currencies of the targeted metal prices
	  * @param newCurrency A new currency to assign
	  * @return Whether any metal price was affected
	  */
	def currency_=(newCurrency: Currency)(implicit connection: Connection) = 
		putColumn(model.currencyColumn, newCurrency.id)
	
	/**
	  * Updates the dates of the targeted metal prices
	  * @param newDate A new date to assign
	  * @return Whether any metal price was affected
	  */
	def date_=(newDate: LocalDate)(implicit connection: Connection) = putColumn(model.dateColumn, newDate)
	
	/**
	  * Updates the metals of the targeted metal prices
	  * @param newMetal A new metal to assign
	  * @return Whether any metal price was affected
	  */
	def metal_=(newMetal: Metal)(implicit connection: Connection) = putColumn(model.metalColumn, newMetal.id)
	
	/**
	  * Updates the price per troy ounces of the targeted metal prices
	  * @param newPricePerTroyOunce A new price per troy ounce to assign
	  * @return Whether any metal price was affected
	  */
	def pricePerTroyOunce_=(newPricePerTroyOunce: Double)(implicit connection: Connection) = 
		putColumn(model.pricePerTroyOunceColumn, newPricePerTroyOunce)
}

