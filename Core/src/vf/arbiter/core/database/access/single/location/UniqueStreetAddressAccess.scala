package vf.arbiter.core.database.access.single.location

import java.time.Instant
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.location.StreetAddressFactory
import vf.arbiter.core.database.model.location.StreetAddressModel
import vf.arbiter.core.model.stored.location.StreetAddress

/**
  * A common trait for access points that return individual and distinct StreetAddresses.
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait UniqueStreetAddressAccess 
	extends SingleRowModelAccess[StreetAddress] 
		with DistinctModelAccess[StreetAddress, Option[StreetAddress], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the postal_code linked with this StreetAddress. None if no instance (or value) was found.
	  */
	def postalCodeId(implicit connection: Connection) = pullColumn(model.postalCodeIdColumn).int
	
	/**
	  * Name of the street -portion of this address. None if no instance (or value) was found.
	  */
	def streetName(implicit connection: Connection) = pullColumn(model.streetNameColumn).string
	
	/**
	  * Number of the targeted building within the specified street. None if no instance (or value) was found.
	  */
	def buildingNumber(implicit connection: Connection) = pullColumn(model.buildingNumberColumn).string
	
	/**
	  * Number or letter of the targeted stair within that building, 
		if applicable. None if no instance (or value) was found.
	  */
	def stair(implicit connection: Connection) = pullColumn(model.stairColumn).string
	
	/**
	  * Number of the targeted room within that stair / building, 
		if applicable. None if no instance (or value) was found.
	  */
	def roomNumber(implicit connection: Connection) = pullColumn(model.roomNumberColumn).string
	
	/**
	  * Id of the user who registered this address. None if no instance (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this StreetAddress was first created. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = StreetAddressModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = StreetAddressFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the buildingNumber of the targeted StreetAddress instance(s)
	  * @param newBuildingNumber A new buildingNumber to assign
	  * @return Whether any StreetAddress instance was affected
	  */
	def buildingNumber_=(newBuildingNumber: String)(implicit connection: Connection) = 
		putColumn(model.buildingNumberColumn, newBuildingNumber)
	
	/**
	  * Updates the created of the targeted StreetAddress instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any StreetAddress instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted StreetAddress instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any StreetAddress instance was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the postalCodeId of the targeted StreetAddress instance(s)
	  * @param newPostalCodeId A new postalCodeId to assign
	  * @return Whether any StreetAddress instance was affected
	  */
	def postalCodeId_=(newPostalCodeId: Int)(implicit connection: Connection) = 
		putColumn(model.postalCodeIdColumn, newPostalCodeId)
	
	/**
	  * Updates the roomNumber of the targeted StreetAddress instance(s)
	  * @param newRoomNumber A new roomNumber to assign
	  * @return Whether any StreetAddress instance was affected
	  */
	def roomNumber_=(newRoomNumber: String)(implicit connection: Connection) = 
		putColumn(model.roomNumberColumn, newRoomNumber)
	
	/**
	  * Updates the stair of the targeted StreetAddress instance(s)
	  * @param newStair A new stair to assign
	  * @return Whether any StreetAddress instance was affected
	  */
	def stair_=(newStair: String)(implicit connection: Connection) = putColumn(model.stairColumn, newStair)
	
	/**
	  * Updates the streetName of the targeted StreetAddress instance(s)
	  * @param newStreetName A new streetName to assign
	  * @return Whether any StreetAddress instance was affected
	  */
	def streetName_=(newStreetName: String)(implicit connection: Connection) = 
		putColumn(model.streetNameColumn, newStreetName)
}

