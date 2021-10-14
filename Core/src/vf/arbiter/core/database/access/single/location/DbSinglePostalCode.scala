package vf.arbiter.core.database.access.single.location

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.model.stored.location.PostalCode

/**
  * An access point to individual PostalCodes, based on their id
  * @since 2021-10-14
  */
case class DbSinglePostalCode(id: Int) extends UniquePostalCodeAccess with SingleIntIdModelAccess[PostalCode]

