package vf.arbiter.core.database.factory.invoice

import utopia.flow.datastructure.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.invoice.ItemUnitData
import vf.arbiter.core.model.stored.invoice.ItemUnit

/**
  * Used for reading ItemUnit data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object ItemUnitFactory extends FromValidatedRowModelFactory[ItemUnit]
{
	// IMPLEMENTED	--------------------
	
	override def table = CoreTables.itemUnit
	
	override def defaultOrdering = None
	
	override def fromValidatedModel(valid: Model) =
		ItemUnit(valid("id").getInt, ItemUnitData(valid("categoryId").getInt, valid("multiplier").getDouble))
}

