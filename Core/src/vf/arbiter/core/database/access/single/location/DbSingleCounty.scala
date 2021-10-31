package vf.arbiter.core.database.access.single.location

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.model.stored.location.County

/**
  * An access point to individual Counties, based on their id
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class DbSingleCounty(id: Int) extends UniqueCountyAccess with SingleIntIdModelAccess[County]

