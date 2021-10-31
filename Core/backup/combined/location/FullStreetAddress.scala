package vf.arbiter.core.model.combined.location

import utopia.flow.datastructure.immutable.Constant
import utopia.flow.generic.ValueConversions._
import utopia.flow.util.Extender
import vf.arbiter.core.model.partial.location.StreetAddressData
import vf.arbiter.core.model.stored.location.StreetAddress
import vf.arbiter.core.model.template.Exportable

/**
 * Combines street address and postal code + county information
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
case class FullStreetAddress(address: StreetAddress, postalCode: FullPostalCode)
	extends Extender[StreetAddressData] with Exportable
{
	// COMPUTED ------------------------------
	
	/**
	 * @return Id of this street address
	 */
	def id = address.id
	
	
	// IMPLEMENTED  --------------------------
	
	override def wrapped = address.data
	
	override def toExportModel =
		address.toExportModel + Constant("postal_code", postalCode.toExportModel)
}
