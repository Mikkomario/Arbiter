package vf.arbiter.accounting.database.factory.target

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.accounting.database.ArbiterAccountingTables
import vf.arbiter.accounting.model.partial.target.TypeSpecificAllocationTargetData
import vf.arbiter.accounting.model.stored.target.TypeSpecificAllocationTarget

/**
  * Used for reading type specific allocation target data from the DB
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object TypeSpecificAllocationTargetFactory extends FromValidatedRowModelFactory[TypeSpecificAllocationTarget]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = ArbiterAccountingTables.typeSpecificAllocationTarget
	
	override protected def fromValidatedModel(valid: Model) = 
		TypeSpecificAllocationTarget(valid("id").getInt, 
			TypeSpecificAllocationTargetData(valid("parentId").getInt, valid("typeId").getInt, 
			valid("ratio").getDouble, valid("isMaximum").getBoolean))
}

