package vf.arbiter.accounting.database.access.single.target

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.accounting.model.stored.target.AllocationTarget

/**
  * An access point to individual allocation targets, based on their id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class DbSingleAllocationTarget(id: Int) 
	extends UniqueAllocationTargetAccess with SingleIntIdModelAccess[AllocationTarget]

