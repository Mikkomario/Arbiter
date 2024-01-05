package vf.arbiter.accounting.database.access.single.target

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.accounting.model.stored.target.TypeSpecificAllocationTarget

/**
  * An access point to individual type specific allocation targets, based on their id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class DbSingleTypeSpecificAllocationTarget(id: Int) 
	extends UniqueTypeSpecificAllocationTargetAccess with SingleIntIdModelAccess[TypeSpecificAllocationTarget]

