package vf.arbiter.accounting.database.access.single.target

import utopia.vault.nosql.view.NullDeprecatableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.target.DetailedAllocationTargetFactory
import vf.arbiter.accounting.database.model.target.TypeSpecificAllocationTargetModel
import vf.arbiter.accounting.model.combined.target.DetailedAllocationTarget

object UniqueDetailedAllocationTargetAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueDetailedAllocationTargetAccess = 
		new _UniqueDetailedAllocationTargetAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueDetailedAllocationTargetAccess(condition: Condition) 
		extends UniqueDetailedAllocationTargetAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return distinct detailed allocation targets
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait UniqueDetailedAllocationTargetAccess 
	extends UniqueAllocationTargetAccessLike[DetailedAllocationTarget] 
		with NullDeprecatableView[UniqueDetailedAllocationTargetAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with the linked specific targets
	  */
	protected def specificTargetModel = TypeSpecificAllocationTargetModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DetailedAllocationTargetFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueDetailedAllocationTargetAccess = 
		new UniqueDetailedAllocationTargetAccess._UniqueDetailedAllocationTargetAccess(mergeCondition(filterCondition))
}

