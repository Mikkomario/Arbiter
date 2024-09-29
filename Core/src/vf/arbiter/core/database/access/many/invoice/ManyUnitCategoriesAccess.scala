package vf.arbiter.core.database.access.many.invoice

import utopia.citadel.database.access.many.description.ManyDescribedAccess
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.arbiter.core.database.access.many.description.DbUnitCategoryDescriptions
import vf.arbiter.core.database.factory.invoice.UnitCategoryFactory
import vf.arbiter.core.database.model.invoice.UnitCategoryModel
import vf.arbiter.core.model.combined.invoice.DescribedUnitCategory
import vf.arbiter.core.model.stored.invoice.UnitCategory

object ManyUnitCategoriesAccess extends ViewFactory[ManyUnitCategoriesAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyUnitCategoriesAccess = 
		_ManyUnitCategoriesAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyUnitCategoriesAccess(override val accessCondition: Option[Condition]) 
		extends ManyUnitCategoriesAccess
}

/**
  * A common trait for access points which target multiple UnitCategories at a time
  * @author Mikko Hilpinen
  * @since 31.10.2021
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
	
	override def self = this
	
	override protected def describedFactory = DescribedUnitCategory
	
	override protected def manyDescriptionsAccess = DbUnitCategoryDescriptions
	
	override def apply(condition: Condition): ManyUnitCategoriesAccess = ManyUnitCategoriesAccess(condition)
	
	override def idOf(item: UnitCategory) = item.id
}

