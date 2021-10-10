package vf.arbiter.core.database.access.single.invoice

import utopia.flow.datastructure.immutable.Value
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
  * @since 2021-10-10
  */
trait UniqueItemUnitAccess 
	extends SingleRowModelAccess[ItemUnit] with DistinctModelAccess[ItemUnit, Option[ItemUnit], Value] 
		with Indexed
{
	// COMPUTED	--------------------
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = ItemUnitModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ItemUnitFactory
}
