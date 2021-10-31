package vf.arbiter.core.database.access.many.invoice

import utopia.citadel.database.access.many.description.ManyDescribedAccessByIds
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.model.combined.invoice.DescribedItemUnit
import vf.arbiter.core.model.stored.invoice.ItemUnit

/**
  * The root access point when targeting multiple ItemUnits at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbItemUnits extends ManyItemUnitsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted ItemUnits
	  * @return An access point to ItemUnits with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbItemUnitsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbItemUnitsSubset(override val ids: Set[Int]) 
		extends ManyItemUnitsAccess with ManyDescribedAccessByIds[ItemUnit, DescribedItemUnit]
}

