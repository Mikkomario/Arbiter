package vf.arbiter.core.database.factory.invoice

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.invoice.ItemUnitData
import vf.arbiter.core.model.stored.invoice.ItemUnit

/**
  * Used for reading ItemUnit data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
object ItemUnitFactory extends FromValidatedRowModelFactory[ItemUnit]
{
	// IMPLEMENTED	--------------------
	
	override def table = CoreTables.itemUnit
	
	override def fromValidatedModel(valid: Model[Constant]) = ItemUnit(valid("id").getInt, ItemUnitData())
}

