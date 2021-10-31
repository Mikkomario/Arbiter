package vf.arbiter.core.database.access.single.invoice

import utopia.citadel.database.access.single.description.SingleIdDescribedAccess
import vf.arbiter.core.database.access.many.description.DbItemUnitDescriptions
import vf.arbiter.core.database.access.single.description.DbItemUnitDescription
import vf.arbiter.core.model.combined.invoice.DescribedItemUnit
import vf.arbiter.core.model.stored.invoice.ItemUnit

/**
  * An access point to individual ItemUnits, based on their id
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class DbSingleItemUnit(id: Int) 
	extends UniqueItemUnitAccess with SingleIdDescribedAccess[ItemUnit, DescribedItemUnit]
{
	// IMPLEMENTED	--------------------
	
	override protected def describedFactory = DescribedItemUnit
	
	override protected def manyDescriptionsAccess = DbItemUnitDescriptions
	
	override protected def singleDescriptionAccess = DbItemUnitDescription
}

