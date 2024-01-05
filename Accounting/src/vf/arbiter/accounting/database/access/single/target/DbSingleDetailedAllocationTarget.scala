package vf.arbiter.accounting.database.access.single.target

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.accounting.model.combined.target.DetailedAllocationTarget

/**
  * An access point to individual detailed allocation targets, based on their target id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class DbSingleDetailedAllocationTarget(id: Int) 
	extends UniqueDetailedAllocationTargetAccess with SingleIntIdModelAccess[DetailedAllocationTarget]

