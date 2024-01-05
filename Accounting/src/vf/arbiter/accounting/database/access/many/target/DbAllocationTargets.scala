package vf.arbiter.accounting.database.access.many.target

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{NonDeprecatedView, UnconditionalView}
import vf.arbiter.accounting.model.stored.target.AllocationTarget

/**
  * The root access point when targeting multiple allocation targets at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbAllocationTargets extends ManyAllocationTargetsAccess with NonDeprecatedView[AllocationTarget]
{
	// COMPUTED	--------------------
	
	/**
	  * A copy of this access point that includes historical (i.e. deprecated) allocation targets
	  */
	def includingHistory = DbAllocationTargetsIncludingHistory
	
	
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted allocation targets
	  * @return An access point to allocation targets with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbAllocationTargetsSubset(ids)
	
	
	// NESTED	--------------------
	
	object DbAllocationTargetsIncludingHistory extends ManyAllocationTargetsAccess with UnconditionalView
	
	class DbAllocationTargetsSubset(targetIds: Set[Int]) extends ManyAllocationTargetsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

