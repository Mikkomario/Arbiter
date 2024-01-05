package vf.arbiter.accounting.database.access.single.target

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.NullDeprecatableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.target.AllocationTargetFactory
import vf.arbiter.accounting.model.stored.target.AllocationTarget

object UniqueAllocationTargetAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueAllocationTargetAccess =
		 new _UniqueAllocationTargetAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueAllocationTargetAccess(condition: Condition) extends UniqueAllocationTargetAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct allocation targets.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait UniqueAllocationTargetAccess 
	extends UniqueAllocationTargetAccessLike[AllocationTarget] with SingleRowModelAccess[AllocationTarget] 
		with NullDeprecatableView[UniqueAllocationTargetAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = AllocationTargetFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueAllocationTargetAccess = 
		new UniqueAllocationTargetAccess._UniqueAllocationTargetAccess(mergeCondition(filterCondition))
}

