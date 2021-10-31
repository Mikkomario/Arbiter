package vf.arbiter.core.database.access.single.invoice

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.invoice.ItemUnitFactory
import vf.arbiter.core.database.model.invoice.ItemUnitModel
import vf.arbiter.core.model.stored.invoice.ItemUnit

/**
  * A common trait for access points that return individual and distinct ItemUnits.
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait UniqueItemUnitAccess 
	extends SingleRowModelAccess[ItemUnit] with DistinctModelAccess[ItemUnit, Option[ItemUnit], Value] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the category this unit belongs to. None if no instance (or value) was found.
	  */
	def categoryId(implicit connection: Connection) = pullColumn(model.categoryIdColumn).int
	
	/**
	  * A multiplier that, when applied to this unit, makes it comparable 
		with the other units in the same category. None if no instance (or value) was found.
	  */
	def multiplier(implicit connection: Connection) = pullColumn(model.multiplierColumn).double
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = ItemUnitModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ItemUnitFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the categoryId of the targeted ItemUnit instance(s)
	  * @param newCategoryId A new categoryId to assign
	  * @return Whether any ItemUnit instance was affected
	  */
	def categoryId_=(newCategoryId: Int)(implicit connection: Connection) = 
		putColumn(model.categoryIdColumn, newCategoryId)
	
	/**
	  * Updates the multiplier of the targeted ItemUnit instance(s)
	  * @param newMultiplier A new multiplier to assign
	  * @return Whether any ItemUnit instance was affected
	  */
	def multiplier_=(newMultiplier: Double)(implicit connection: Connection) = 
		putColumn(model.multiplierColumn, newMultiplier)
}

