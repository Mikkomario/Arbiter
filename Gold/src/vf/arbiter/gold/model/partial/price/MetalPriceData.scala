package vf.arbiter.gold.model.partial.price

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.DoubleType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.LocalDateType
import utopia.flow.generic.model.template.ModelConvertible
import vf.arbiter.gold.model.cached.price.WeightPrice
import vf.arbiter.gold.model.enumeration.Currency.Euro
import vf.arbiter.gold.model.enumeration.Metal.Gold
import vf.arbiter.gold.model.enumeration.WeightUnit.TroyOunce
import vf.arbiter.gold.model.enumeration.{Currency, Metal}

import java.time.LocalDate

object MetalPriceData extends FromModelFactoryWithSchema[MetalPriceData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("metal", IntType, Vector(), Gold.id), 
			PropertyDeclaration("currency", IntType, Vector(), Euro.id), PropertyDeclaration("date", 
			LocalDateType, isOptional = true), PropertyDeclaration("pricePerTroyOunce", DoubleType, 
			Vector("price_per_troy_ounce"))))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		MetalPriceData(Metal.fromValue(valid("metal")), Currency.fromValue(valid("currency")), 
			valid("date").getLocalDate, valid("pricePerTroyOunce").getDouble)
}

/**
  * Documents a metal's (average) price on a specific date
  * @param metal Metal who's price is recorded
  * @param currency The currency in which the price is given
  * @param date Date on which the price was used
  * 
	@param pricePerTroyOunce Price of the specified metal in the specified currency. Per one troy ounce of metal.
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
case class MetalPriceData(metal: Metal, currency: Currency, date: LocalDate, pricePerTroyOunce: Double) 
	extends ModelConvertible
{
	// ATTRIBUTES   --------------------
	
	/**
	 * @return The price of the specified metal on the selected date
	 */
	def price = WeightPrice(pricePerTroyOunce, TroyOunce)
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("metal" -> metal.id, "currency" -> currency.id, "date" -> date, 
			"pricePerTroyOunce" -> pricePerTroyOunce))
}

