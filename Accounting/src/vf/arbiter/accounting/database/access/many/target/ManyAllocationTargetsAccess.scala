package vf.arbiter.accounting.database.access.many.target

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.target.AllocationTargetFactory
import vf.arbiter.accounting.model.stored.target.AllocationTarget

object ManyAllocationTargetsAccess extends ViewFactory[ManyAllocationTargetsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyAllocationTargetsAccess = 
		_ManyAllocationTargetsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyAllocationTargetsAccess(override val accessCondition: Option[Condition]) 
		extends ManyAllocationTargetsAccess
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
	
	override def self = this
	
	
	// OTHER	--------------------
	
	def apply(condition: Condition): ManyAllocationTargetsAccess = ManyAllocationTargetsAccess(condition)
}

