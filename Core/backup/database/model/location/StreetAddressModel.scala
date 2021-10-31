package vf.arbiter.core.database.model.location

import java.time.Instant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.location.StreetAddressFactory
import vf.arbiter.core.model.partial.location.StreetAddressData
import vf.arbiter.core.model.stored.location.StreetAddress

/**
  * Used for constructing StreetAddressModel instances and for inserting StreetAddresss to the database
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object StreetAddressModel extends DataInserter[StreetAddressModel, StreetAddress, StreetAddressData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains StreetAddress postalCodeId
	  */
	val postalCodeIdAttName = "postalCodeId"
	
	/**
	  * Name of the property that contains StreetAddress streetName
	  */
	val streetNameAttName = "streetName"
	
	/**
	  * Name of the property that contains StreetAddress buildingNumber
	  */
	val buildingNumberAttName = "buildingNumber"
	
	/**
	  * Name of the property that contains StreetAddress stair
	  */
	val stairAttName = "stair"
	
	/**
	  * Name of the property that contains StreetAddress roomNumber
	  */
	val roomNumberAttName = "roomNumber"
	
	/**
	  * Name of the property that contains StreetAddress creatorId
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains StreetAddress created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains StreetAddress postalCodeId
	  */
	def postalCodeIdColumn = table(postalCodeIdAttName)
	
	/**
	  * Column that contains StreetAddress streetName
	  */
	def streetNameColumn = table(streetNameAttName)
	
	/**
	  * Column that contains StreetAddress buildingNumber
	  */
	def buildingNumberColumn = table(buildingNumberAttName)
	
	/**
	  * Column that contains StreetAddress stair
	  */
	def stairColumn = table(stairAttName)
	
	/**
	  * Column that contains StreetAddress roomNumber
	  */
	def roomNumberColumn = table(roomNumberAttName)
	
	/**
	  * Column that contains StreetAddress creatorId
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains StreetAddress created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = StreetAddressFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: StreetAddressData) = 
		apply(None, Some(data.postalCodeId), Some(data.streetName), Some(data.buildingNumber), data.stair, 
			data.roomNumber, data.creatorId, Some(data.created))
	
	override def complete(id: Value, data: StreetAddressData) = StreetAddress(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param buildingNumber Number of the targeted building within the specified street
	  * @return A model containing only the specified buildingNumber
	  */
	def withBuildingNumber(buildingNumber: String) = apply(buildingNumber = Some(buildingNumber))
	
	/**
	  * @param created Time when this StreetAddress was first created
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who registered this address
	  * @return A model containing only the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param id A StreetAddress id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param postalCodeId Id of the postal_code linked with this StreetAddress
	  * @return A model containing only the specified postalCodeId
	  */
	def withPostalCodeId(postalCodeId: Int) = apply(postalCodeId = Some(postalCodeId))
	
	/**
	  * @param roomNumber Number of the targeted room within that stair / building, if applicable
	  * @return A model containing only the specified roomNumber
	  */
	def withRoomNumber(roomNumber: String) = apply(roomNumber = Some(roomNumber))
	
	/**
	  * @param stair Number or letter of the targeted stair within that building, if applicable
	  * @return A model containing only the specified stair
	  */
	def withStair(stair: String) = apply(stair = Some(stair))
	
	/**
	  * @param streetName Name of the street -portion of this address
	  * @return A model containing only the specified streetName
	  */
	def withStreetName(streetName: String) = apply(streetName = Some(streetName))
}

/**
  * Used for interacting with StreetAddresses in the database
  * @param id StreetAddress database id
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
case class StreetAddressModel(id: Option[Int] = None, postalCodeId: Option[Int] = None, 
	streetName: Option[String] = None, buildingNumber: Option[String] = None, stair: Option[String] = None, 
	roomNumber: Option[String] = None, creatorId: Option[Int] = None, created: Option[Instant] = None) 
	extends StorableWithFactory[StreetAddress]
{
	// IMPLEMENTED	--------------------
	
	override def factory = StreetAddressModel.factory
	
	override def valueProperties = 
	{
		import StreetAddressModel._
		Vector("id" -> id, postalCodeIdAttName -> postalCodeId, streetNameAttName -> streetName, 
			buildingNumberAttName -> buildingNumber, stairAttName -> stair, roomNumberAttName -> roomNumber, 
			creatorIdAttName -> creatorId, createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param buildingNumber A new buildingNumber
	  * @return A new copy of this model with the specified buildingNumber
	  */
	def withBuildingNumber(buildingNumber: String) = copy(buildingNumber = Some(buildingNumber))
	
	/**
	  * @param created A new created
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param creatorId A new creatorId
	  * @return A new copy of this model with the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = copy(creatorId = Some(creatorId))
	
	/**
	  * @param postalCodeId A new postalCodeId
	  * @return A new copy of this model with the specified postalCodeId
	  */
	def withPostalCodeId(postalCodeId: Int) = copy(postalCodeId = Some(postalCodeId))
	
	/**
	  * @param roomNumber A new roomNumber
	  * @return A new copy of this model with the specified roomNumber
	  */
	def withRoomNumber(roomNumber: String) = copy(roomNumber = Some(roomNumber))
	
	/**
	  * @param stair A new stair
	  * @return A new copy of this model with the specified stair
	  */
	def withStair(stair: String) = copy(stair = Some(stair))
	
	/**
	  * @param streetName A new streetName
	  * @return A new copy of this model with the specified streetName
	  */
	def withStreetName(streetName: String) = copy(streetName = Some(streetName))
}

