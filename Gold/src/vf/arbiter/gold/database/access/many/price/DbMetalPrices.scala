package vf.arbiter.gold.database.access.many.price

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple metal prices at a time
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
object DbMetalPrices extends ManyMetalPricesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted metal prices
	  * @return An access point to metal prices with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbMetalPricesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbMetalPricesSubset(targetIds: Set[Int]) extends ManyMetalPricesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

