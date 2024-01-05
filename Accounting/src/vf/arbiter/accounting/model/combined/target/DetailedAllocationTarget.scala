package vf.arbiter.accounting.model.combined.target

import utopia.flow.view.template.Extender
import vf.arbiter.accounting.model.partial.target.AllocationTargetData
import vf.arbiter.accounting.model.stored.target.{AllocationTarget, TypeSpecificAllocationTarget}

/**
  * Contains the full target, including type-specific values
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class DetailedAllocationTarget(target: AllocationTarget, 
	specificTargets: Vector[TypeSpecificAllocationTarget]) 
	extends Extender[AllocationTargetData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this target in the database
	  */
	def id = target.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = target.data
}

