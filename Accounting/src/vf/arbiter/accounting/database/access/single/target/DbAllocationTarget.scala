package vf.arbiter.accounting.database.access.single.target

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NonDeprecatedView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.target.AllocationTargetFactory
import vf.arbiter.accounting.database.model.target.AllocationTargetModel
import vf.arbiter.accounting.model.stored.target.AllocationTarget

/**
  * Used for accessing individual allocation targets
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbAllocationTarget 
	extends SingleRowModelAccess[AllocationTarget] with NonDeprecatedView[AllocationTarget] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AllocationTargetModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AllocationTargetFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted allocation target
	  * @return An access point to that allocation target
	  */
	def apply(id: Int) = DbSingleAllocationTarget(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique allocation targets.
	  * @return An access point to the allocation target that satisfies the specified condition
	  */
	protected
		 def filterDistinct(condition: Condition) = UniqueAllocationTargetAccess(mergeCondition(condition))
}

