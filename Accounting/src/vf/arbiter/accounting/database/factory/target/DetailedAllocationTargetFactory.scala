package vf.arbiter.accounting.database.factory.target

import utopia.vault.nosql.factory.multi.MultiCombiningFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.accounting.model.combined.target.DetailedAllocationTarget
import vf.arbiter.accounting.model.stored.target.{AllocationTarget, TypeSpecificAllocationTarget}

/**
  * Used for reading detailed allocation targets from the database
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DetailedAllocationTargetFactory 
	extends MultiCombiningFactory[DetailedAllocationTarget, AllocationTarget, TypeSpecificAllocationTarget] 
		with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = TypeSpecificAllocationTargetFactory
	
	override def isAlwaysLinked = false
	
	override def nonDeprecatedCondition = parentFactory.nonDeprecatedCondition
	
	override def parentFactory = AllocationTargetFactory
	
	override def apply(target: AllocationTarget, specificTargets: Vector[TypeSpecificAllocationTarget]) = 
		DetailedAllocationTarget(target, specificTargets)
}

