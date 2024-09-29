package vf.arbiter.core.database.access.many.invoice

import utopia.citadel.database.access.many.description.ManyDescribedAccess
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.arbiter.core.database.access.many.description.DbItemUnitDescriptions
import vf.arbiter.core.database.factory.invoice.ItemUnitFactory
import vf.arbiter.core.database.model.invoice.ItemUnitModel
import vf.arbiter.core.model.combined.invoice.DescribedItemUnit
import vf.arbiter.core.model.stored.invoice.ItemUnit

object ManyItemUnitsAccess extends ViewFactory[ManyItemUnitsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyItemUnitsAccess = _ManyItemUnitsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyItemUnitsAccess(override val accessCondition: Option[Condition]) 
		extends ManyItemUnitsAccess
}

/**
  * A common trait for access points which target multiple ItemUnits at a time
  * @author Mikko Hilpinen
  * @since 31.10.2021
  */
trait ManyItemUnitsAccess 
	extends ManyRowModelAccess[ItemUnit] with ManyDescribedAccess[ItemUnit, DescribedItemUnit] 
		with FilterableView[ManyItemUnitsAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * categoryIds of the accessible ItemUnits
	  */
	def categoryIds(implicit connection: Connection) = 
		pullColumn(model.categoryIdColumn).flatMap { value => value.int }
	
	/**
	  * multipliers of the accessible ItemUnits
	  */
	def multipliers(implicit connection: Connection) = 
		pullColumn(model.multiplierColumn).flatMap { value => value.double }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = ItemUnitModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ItemUnitFactory
	
	override def self = this
	
	override protected def describedFactory = DescribedItemUnit
	
	override protected def manyDescriptionsAccess = DbItemUnitDescriptions
	
	override def apply(condition: Condition): ManyItemUnitsAccess = ManyItemUnitsAccess(condition)
	
	override def idOf(item: ItemUnit) = item.id
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the categoryId of the targeted ItemUnit instance(s)
	  * @param newCategoryId A new categoryId to assign
	  * @return Whether any ItemUnit instance was affected
	  */
	def categoryIds_=(newCategoryId: Int)(implicit connection: Connection) = 
		putColumn(model.categoryIdColumn, newCategoryId)
	
	/**
	  * Updates the multiplier of the targeted ItemUnit instance(s)
	  * @param newMultiplier A new multiplier to assign
	  * @return Whether any ItemUnit instance was affected
	  */
	def multipliers_=(newMultiplier: Double)(implicit connection: Connection) = 
		putColumn(model.multiplierColumn, newMultiplier)
}

