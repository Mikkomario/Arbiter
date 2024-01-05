package vf.arbiter.accounting.model.stored.target

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.accounting.database.access.single.target.DbSingleTypeSpecificAllocationTarget
import vf.arbiter.accounting.model.partial.target.TypeSpecificAllocationTargetData

/**
  * Represents a type specific allocation target that has already been stored in the database
  * @param id id of this type specific allocation target in the database
  * @param data Wrapped type specific allocation target data
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class TypeSpecificAllocationTarget(id: Int, data: TypeSpecificAllocationTargetData) 
	extends StoredModelConvertible[TypeSpecificAllocationTargetData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this type specific allocation target in the database
	  */
	def access = DbSingleTypeSpecificAllocationTarget(id)
}

