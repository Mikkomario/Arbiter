package vf.arbiter.accounting.model.stored.target

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.accounting.database.access.single.target.DbSingleAllocationTarget
import vf.arbiter.accounting.model.partial.target.AllocationTargetData

/**
  * Represents a allocation target that has already been stored in the database
  * @param id id of this allocation target in the database
  * @param data Wrapped allocation target data
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class AllocationTarget(id: Int, data: AllocationTargetData) 
	extends StoredModelConvertible[AllocationTargetData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this allocation target in the database
	  */
	def access = DbSingleAllocationTarget(id)
}

