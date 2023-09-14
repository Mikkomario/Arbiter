package vf.arbiter.gold.database.factory.price

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.gold.database.ArbiterGoldTables
import vf.arbiter.gold.model.enumeration.{Currency, Metal}
import vf.arbiter.gold.model.partial.price.MetalPriceData
import vf.arbiter.gold.model.stored.price.MetalPrice

/**
  * Used for reading metal price data from the DB
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
object MetalPriceFactory extends FromValidatedRowModelFactory[MetalPrice]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = ArbiterGoldTables.metalPrice
	
	override protected def fromValidatedModel(valid: Model) = 
		MetalPrice(valid("id").getInt, MetalPriceData(Metal.fromValue(valid("metalId")), 
			Currency.fromValue(valid("currencyId")), valid("date").getLocalDate, 
			valid("pricePerTroyOunce").getDouble))
}

