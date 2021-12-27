package vf.arbiter.core.database.access.many.invoice

import utopia.citadel.database.access.many.description.ManyDescribedAccess
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.{FilterableView, SubView}
import utopia.vault.sql.Condition
import vf.arbiter.core.database.access.many.description.DbUnitCategoryDescriptions
import vf.arbiter.core.database.factory.invoice.UnitCategoryFactory
import vf.arbiter.core.database.model.invoice.UnitCategoryModel
import vf.arbiter.core.model.combined.invoice.DescribedUnitCategory
import vf.arbiter.core.model.stored.invoice.UnitCategory

object ManyUnitCategoriesAccess
{
	// NESTED	--------------------
	
	private class ManyUnitCategoriesSubView(override val parent: ManyRowModelAccess[UnitCategory], 
		override val filterCondition: Condition) 
		extends ManyUnitCategoriesAccess with SubView
}

/**
  * A common trait for access points which target multiple UnitCategories at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait ManyUnitCategoriesAccess 
	extends ManyRowModelAccess[UnitCategory] with ManyDescribedAccess[UnitCategory, DescribedUnitCategory]
		with FilterableView[ManyUnitCategoriesAccess]
{
	// COMPUTED	--------------------
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = UnitCategoryModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = UnitCategoryFactory
	
	override protected def describedFactory = DescribedUnitCategory
	
	override protected def manyDescriptionsAccess = DbUnitCategoryDescriptions
	
	override def filter(additionalCondition: Condition): ManyUnitCategoriesAccess = 
		new ManyUnitCategoriesAccess.ManyUnitCategoriesSubView(this, additionalCondition)
	
	override def idOf(item: UnitCategory) = item.id
}

