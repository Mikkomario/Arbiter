package vf.arbiter.gold.database.access.single.price

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.arbiter.gold.database.factory.price.MetalPriceFactory
import vf.arbiter.gold.database.model.price.MetalPriceModel
import vf.arbiter.gold.model.stored.price.MetalPrice

/**
  * Used for accessing individual metal prices
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
object DbMetalPrice extends SingleRowModelAccess[MetalPrice] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MetalPriceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MetalPriceFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted metal price
	  * @return An access point to that metal price
	  */
	def apply(id: Int) = DbSingleMetalPrice(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique metal prices.
	  * @return An access point to the metal price that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueMetalPriceAccess(mergeCondition(condition))
}

