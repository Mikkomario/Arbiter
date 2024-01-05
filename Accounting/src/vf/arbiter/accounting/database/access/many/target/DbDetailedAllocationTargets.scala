package vf.arbiter.accounting.database.access.many.target

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{NonDeprecatedView, UnconditionalView}
import vf.arbiter.accounting.model.combined.target.DetailedAllocationTarget

/**
  * The root access point when targeting multiple detailed allocation targets at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbDetailedAllocationTargets 
	extends ManyDetailedAllocationTargetsAccess with NonDeprecatedView[DetailedAllocationTarget]
{
	// COMPUTED	--------------------
	
	/**
	  * A copy of this access point that includes historical (i.e. deprecated) detailed allocation targets
	  */
	def includingHistory = DbDetailedAllocationTargetsIncludingHistory
	
	
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted detailed allocation targets
	  * @return An access point to detailed allocation targets with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbDetailedAllocationTargetsSubset(ids)
	
	
	// NESTED	--------------------
	
	object DbDetailedAllocationTargetsIncludingHistory 
		extends ManyDetailedAllocationTargetsAccess with UnconditionalView
	
	class DbDetailedAllocationTargetsSubset(targetIds: Set[Int]) extends ManyDetailedAllocationTargetsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

