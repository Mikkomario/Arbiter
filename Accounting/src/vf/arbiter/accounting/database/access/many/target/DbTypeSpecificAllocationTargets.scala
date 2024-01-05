package vf.arbiter.accounting.database.access.many.target

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple type specific allocation targets at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbTypeSpecificAllocationTargets extends ManyTypeSpecificAllocationTargetsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted type specific allocation targets
	  * @return An access point to type specific allocation targets with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbTypeSpecificAllocationTargetsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbTypeSpecificAllocationTargetsSubset(targetIds: Set[Int]) 
		extends ManyTypeSpecificAllocationTargetsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

