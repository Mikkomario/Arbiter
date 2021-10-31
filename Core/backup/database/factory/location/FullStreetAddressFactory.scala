package vf.arbiter.core.database.factory.location

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import vf.arbiter.core.model.combined.location.{FullPostalCode, FullStreetAddress}
import vf.arbiter.core.model.stored.location.StreetAddress

/**
 * Used for reading street address data from the DB
 * @author Mikko Hilpinen
 * @since 16.10.2021, v0.2
 */
object FullStreetAddressFactory extends CombiningFactory[FullStreetAddress, StreetAddress, FullPostalCode]
{
	override def parentFactory = StreetAddressFactory
	
	override def childFactory = FullPostalCodeFactory
	
	override def apply(parent: StreetAddress, child: FullPostalCode) = FullStreetAddress(parent, child)
}
