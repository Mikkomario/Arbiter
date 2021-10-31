package vf.arbiter.core.database.access.single.location

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.database.factory.location.FullStreetAddressFactory
import vf.arbiter.core.model.stored.location.StreetAddress

/**
  * An access point to individual StreetAddresses, based on their id
  * @since 2021-10-14
  */
case class DbSingleStreetAddress(id: Int) 
	extends UniqueStreetAddressAccess with SingleIntIdModelAccess[StreetAddress]
{
	/**
	 * @param connection Implicit Connection
	 * @return Full version of this street address
	 */
	def full(implicit connection: Connection) = FullStreetAddressFactory.get(condition)
}
