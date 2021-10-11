package vf.arbiter.core.database.access.many.invoice

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.invoice.ItemUnitFactory
import vf.arbiter.core.database.model.invoice.ItemUnitModel
import vf.arbiter.core.model.stored.invoice.ItemUnit

/**
  * A common trait for access points which target multiple ItemUnits at a time
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
trait ManyItemUnitsAccess extends ManyRowModelAccess[ItemUnit] with Indexed
{
	// COMPUTED	--------------------
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = ItemUnitModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ItemUnitFactory
	
	override protected def defaultOrdering = None
}

