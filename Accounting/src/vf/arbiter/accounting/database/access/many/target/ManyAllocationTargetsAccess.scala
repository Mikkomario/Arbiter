package vf.arbiter.accounting.database.access.many.target

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.target.AllocationTargetFactory
import vf.arbiter.accounting.model.stored.target.AllocationTarget

object ManyAllocationTargetsAccess
{
	// NESTED	--------------------
	
	private class ManyAllocationTargetsSubView(condition: Condition) extends ManyAllocationTargetsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple allocation targets at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait ManyAllocationTargetsAccess 
	extends ManyAllocationTargetsAccessLike[AllocationTarget, ManyAllocationTargetsAccess] 
		with ManyRowModelAccess[AllocationTarget]
{
	// IMPLEMENTED	--------------------
	
	override def factory = AllocationTargetFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyAllocationTargetsAccess = 
		new ManyAllocationTargetsAccess.ManyAllocationTargetsSubView(mergeCondition(filterCondition))
}

