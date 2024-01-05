package vf.arbiter.accounting.database.factory.target

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.accounting.database.ArbiterAccountingTables
import vf.arbiter.accounting.database.model.target.AllocationTargetModel
import vf.arbiter.accounting.model.partial.target.AllocationTargetData
import vf.arbiter.accounting.model.stored.target.AllocationTarget

/**
  * Used for reading allocation target data from the DB
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object AllocationTargetFactory extends FromValidatedRowModelFactory[AllocationTarget] with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def nonDeprecatedCondition = AllocationTargetModel.nonDeprecatedCondition
	
	override def table = ArbiterAccountingTables.allocationTarget
	
	override protected def fromValidatedModel(valid: Model) = 
		AllocationTarget(valid("id").getInt, AllocationTargetData(valid("companyId").getInt, 
			valid("capitalRatio").getDouble, valid("appliedSince").getInstant, valid("appliedUntil").instant, 
			valid("creatorId").int, valid("created").getInstant, valid("deprecatedAfter").instant))
}

