package vf.arbiter.core.database.access.single.invoice

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.invoice.UnitCategoryFactory
import vf.arbiter.core.database.model.invoice.UnitCategoryModel
import vf.arbiter.core.model.stored.invoice.UnitCategory

/**
  * Used for accessing individual UnitCategories
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbUnitCategory extends SingleRowModelAccess[UnitCategory] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = UnitCategoryModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = UnitCategoryFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted UnitCategory instance
	  * @return An access point to that UnitCategory
	  */
	def apply(id: Int) = DbSingleUnitCategory(id)
}

