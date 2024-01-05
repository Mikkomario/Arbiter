package vf.arbiter.accounting.database.access.single.target

import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NonDeprecatedView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.target.DetailedAllocationTargetFactory
import vf.arbiter.accounting.database.model.target.{AllocationTargetModel, TypeSpecificAllocationTargetModel}
import vf.arbiter.accounting.model.combined.target.DetailedAllocationTarget

/**
  * Used for accessing individual detailed allocation targets
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbDetailedAllocationTarget 
	extends SingleModelAccess[DetailedAllocationTarget] with NonDeprecatedView[DetailedAllocationTarget] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with linked targets
	  */
	protected def model = AllocationTargetModel
	
	/**
	  * A database model (factory) used for interacting with the linked specific targets
	  */
	protected def specificTargetModel = TypeSpecificAllocationTargetModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DetailedAllocationTargetFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted detailed allocation target
	  * @return An access point to that detailed allocation target
	  */
	def apply(id: Int) = DbSingleDetailedAllocationTarget(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique detailed allocation targets.
	  * @return An access point to the detailed allocation target that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = 
		UniqueDetailedAllocationTargetAccess(mergeCondition(condition))
}

