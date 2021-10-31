package vf.arbiter.core.database.access.single.invoice

import utopia.citadel.database.access.single.description.SingleIdDescribedAccess
import vf.arbiter.core.database.access.many.description.DbUnitCategoryDescriptions
import vf.arbiter.core.database.access.single.description.DbUnitCategoryDescription
import vf.arbiter.core.model.combined.invoice.DescribedUnitCategory
import vf.arbiter.core.model.stored.invoice.UnitCategory

/**
  * An access point to individual UnitCategories, based on their id
  * @since 2021-10-14
  */
case class DbSingleUnitCategory(id: Int) 
	extends UniqueUnitCategoryAccess with SingleIdDescribedAccess[UnitCategory, DescribedUnitCategory]
{
	// IMPLEMENTED	--------------------
	
	override protected def describedFactory = DescribedUnitCategory
	
	override protected def manyDescriptionsAccess = DbUnitCategoryDescriptions
	
	override protected def singleDescriptionAccess = DbUnitCategoryDescription
}

