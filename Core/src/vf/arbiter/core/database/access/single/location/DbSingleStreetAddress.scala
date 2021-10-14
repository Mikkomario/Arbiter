package vf.arbiter.core.database.access.single.location

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.model.stored.location.StreetAddress

/**
  * An access point to individual StreetAddresses, based on their id
  * @since 2021-10-14
  */
case class DbSingleStreetAddress(id: Int) 
	extends UniqueStreetAddressAccess with SingleIntIdModelAccess[StreetAddress]

