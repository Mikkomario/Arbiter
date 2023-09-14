package vf.arbiter.gold.database.model.price

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.gold.database.factory.price.MetalPriceFactory
import vf.arbiter.gold.model.enumeration.{Currency, Metal}
import vf.arbiter.gold.model.partial.price.MetalPriceData
import vf.arbiter.gold.model.stored.price.MetalPrice

import java.time.LocalDate

/**
  * Used for constructing MetalPriceModel instances and for inserting metal prices to the database
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
object MetalPriceModel extends DataInserter[MetalPriceModel, MetalPrice, MetalPriceData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains metal price metal
	  */
	val metalAttName = "metalId"
	
	/**
	  * Name of the property that contains metal price currency
	  */
	val currencyAttName = "currencyId"
	
	/**
	  * Name of the property that contains metal price date
	  */
	val dateAttName = "date"
	
	/**
	  * Name of the property that contains metal price price per troy ounce
	  */
	val pricePerTroyOunceAttName = "pricePerTroyOunce"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains metal price metal
	  */
	def metalColumn = table(metalAttName)
	
	/**
	  * Column that contains metal price currency
	  */
	def currencyColumn = table(currencyAttName)
	
	/**
	  * Column that contains metal price date
	  */
	def dateColumn = table(dateAttName)
	
	/**
	  * Column that contains metal price price per troy ounce
	  */
	def pricePerTroyOunceColumn = table(pricePerTroyOunceAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = MetalPriceFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: MetalPriceData) = 
		apply(None, Some(data.metal.id), Some(data.currency.id), Some(data.date), 
			Some(data.pricePerTroyOunce))
	
	override protected def complete(id: Value, data: MetalPriceData) = MetalPrice(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param currency The currency in which the price is given
	  * @return A model containing only the specified currency
	  */
	def withCurrency(currency: Currency) = apply(currency = Some(currency.id))
	
	/**
	  * @param date Date on which the price was used
	  * @return A model containing only the specified date
	  */
	def withDate(date: LocalDate) = apply(date = Some(date))
	
	/**
	  * @param id A metal price id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param metal Metal who's price is recorded
	  * @return A model containing only the specified metal
	  */
	def withMetal(metal: Metal) = apply(metal = Some(metal.id))
	
	/**
	  * 
		@param pricePerTroyOunce Price of the specified metal in the specified currency. Per one troy ounce of metal.
	  * @return A model containing only the specified price per troy ounce
	  */
	def withPricePerTroyOunce(pricePerTroyOunce: Double) = apply(pricePerTroyOunce = Some(pricePerTroyOunce))
}

/**
  * Used for interacting with MetalPrices in the database
  * @param id metal price database id
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
case class MetalPriceModel(id: Option[Int] = None, metal: Option[Int] = None, currency: Option[Int] = None, 
	date: Option[LocalDate] = None, pricePerTroyOunce: Option[Double] = None) 
	extends StorableWithFactory[MetalPrice]
{
	// IMPLEMENTED	--------------------
	
	override def factory = MetalPriceModel.factory
	
	override def valueProperties = {
		import MetalPriceModel._
		Vector("id" -> id, metalAttName -> metal, currencyAttName -> currency, dateAttName -> date, 
			pricePerTroyOunceAttName -> pricePerTroyOunce)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param currency The currency in which the price is given
	  * @return A new copy of this model with the specified currency
	  */
	def withCurrency(currency: Currency) = copy(currency = Some(currency.id))
	
	/**
	  * @param date Date on which the price was used
	  * @return A new copy of this model with the specified date
	  */
	def withDate(date: LocalDate) = copy(date = Some(date))
	
	/**
	  * @param metal Metal who's price is recorded
	  * @return A new copy of this model with the specified metal
	  */
	def withMetal(metal: Metal) = copy(metal = Some(metal.id))
	
	/**
	  * 
		@param pricePerTroyOunce Price of the specified metal in the specified currency. Per one troy ounce of metal.
	  * @return A new copy of this model with the specified price per troy ounce
	  */
	def withPricePerTroyOunce(pricePerTroyOunce: Double) = copy(pricePerTroyOunce = Some(pricePerTroyOunce))
}

