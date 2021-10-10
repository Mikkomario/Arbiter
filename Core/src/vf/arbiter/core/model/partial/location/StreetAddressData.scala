package vf.arbiter.core.model.partial.location

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._

/**
  * Represents a specific street address
  * @param postalCodeId Id of the postal_code linked with this StreetAddress
  * @param streetName Name of the street -portion of this address
  * @param buildingNumber Number of the targeted building within the specified street
  * @param stair Number or letter of the targeted stair within that building, if applicable
  * @param roomNumber Number of the targeted room within that stair / building, if applicable
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class StreetAddressData(postalCodeId: Int, streetName: String, buildingNumber: String, 
	stair: Option[String] = None, roomNumber: Option[String] = None) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("postal_code_id" -> postalCodeId, "street_name" -> streetName, 
			"building_number" -> buildingNumber, "stair" -> stair, "room_number" -> roomNumber))
}

