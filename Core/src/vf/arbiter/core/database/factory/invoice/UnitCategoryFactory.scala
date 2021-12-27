package vf.arbiter.core.database.factory.invoice

import utopia.flow.datastructure.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.invoice.UnitCategoryData
import vf.arbiter.core.model.stored.invoice.UnitCategory

/**
  * Used for reading UnitCategory data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object UnitCategoryFactory extends FromValidatedRowModelFactory[UnitCategory]
{
	// IMPLEMENTED	--------------------
	
	override def table = CoreTables.unitCategory
	
	override def defaultOrdering = None
	
	override def fromValidatedModel(valid: Model) = UnitCategory(valid("id").getInt,
		UnitCategoryData())
}

