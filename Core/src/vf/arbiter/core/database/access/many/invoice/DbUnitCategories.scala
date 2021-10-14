package vf.arbiter.core.database.access.many.invoice

import utopia.citadel.database.access.many.description.ManyDescribedAccessByIds
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.model.combined.invoice.DescribedUnitCategory
import vf.arbiter.core.model.stored.invoice.UnitCategory

/**
  * The root access point when targeting multiple UnitCategories at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbUnitCategories extends ManyUnitCategoriesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted UnitCategories
	  * @return An access point to UnitCategories with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbUnitCategoriesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbUnitCategoriesSubset(override val ids: Set[Int]) 
		extends ManyUnitCategoriesAccess with ManyDescribedAccessByIds[UnitCategory, DescribedUnitCategory]
}

