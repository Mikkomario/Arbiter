package vf.arbiter.core.database.factory.invoice

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.invoice.UnitCategoryData
import vf.arbiter.core.model.stored.invoice.UnitCategory

/**
  * Used for reading UnitCategory data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object UnitCategoryFactory extends FromValidatedRowModelFactory[UnitCategory]
{
	// IMPLEMENTED	--------------------
	
	override def table = CoreTables.unitCategory
	
	override def fromValidatedModel(valid: Model[Constant]) = UnitCategory(valid("id").getInt, 
		UnitCategoryData())
}

