package vf.arbiter.core.model.partial.location

import java.time.Instant
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now
import vf.arbiter.core.model.template.Exportable

/**
  * Represents a specific street address
  * @param postalCodeId Id of the postal_code linked with this StreetAddress
  * @param streetName Name of the street -portion of this address
  * @param buildingNumber Number of the targeted building within the specified street
  * @param stair Number or letter of the targeted stair within that building, if applicable
  * @param roomNumber Number of the targeted room within that stair / building, if applicable
  * @param creatorId Id of the user who registered this address
  * @param created Time when this StreetAddress was first created
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class StreetAddressData(postalCodeId: Int, streetName: String, buildingNumber: String, 
	stair: Option[String] = None, roomNumber: Option[String] = None, creatorId: Option[Int] = None, 
	created: Instant = Now) 
	extends ModelConvertible with Exportable
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("postal_code_id" -> postalCodeId, "street_name" -> streetName, 
			"building_number" -> buildingNumber, "stair" -> stair, "room_number" -> roomNumber, 
			"creator_id" -> creatorId, "created" -> created))
	
	override def toExportModel =
		Model(Vector("street_name" -> streetName, "building_number" -> buildingNumber, "stair" -> stair,
			"room_number" -> roomNumber))
	
	override def toString =
	{
		val stairPart = stair.map { " " + _ }.getOrElse("")
		val roomNumberPart = roomNumber.map { " " + _ }.getOrElse("")
		s"$streetName $buildingNumber$stairPart$roomNumberPart"
	}
}

