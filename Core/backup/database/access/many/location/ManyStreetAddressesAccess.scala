package vf.arbiter.core.database.access.many.location

import java.time.Instant
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.location.StreetAddressFactory
import vf.arbiter.core.database.model.location.StreetAddressModel
import vf.arbiter.core.model.stored.location.StreetAddress

object ManyStreetAddressesAccess
{
	// NESTED	--------------------
	
	private class ManyStreetAddressesSubView(override val parent: ManyRowModelAccess[StreetAddress], 
		override val filterCondition: Condition) 
		extends ManyStreetAddressesAccess with SubView
}

/**
  * A common trait for access points which target multiple StreetAddresses at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
trait ManyStreetAddressesAccess extends ManyRowModelAccess[StreetAddress] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * postalCodeIds of the accessible StreetAddresses
	  */
	def postalCodeIds(implicit connection: Connection) = 
		pullColumn(model.postalCodeIdColumn).flatMap { value => value.int }
	
	/**
	  * streetNames of the accessible StreetAddresses
	  */
	def streetNames(implicit connection: Connection) = 
		pullColumn(model.streetNameColumn).flatMap { value => value.string }
	
	/**
	  * buildingNumbers of the accessible StreetAddresses
	  */
	def buildingNumbers(implicit connection: Connection) = 
		pullColumn(model.buildingNumberColumn).flatMap { value => value.string }
	
	/**
	  * stairs of the accessible StreetAddresses
	  */
	def stairs(implicit connection: Connection) = pullColumn(model.stairColumn)
		.flatMap { value => value.string }
	
	/**
	  * roomNumbers of the accessible StreetAddresses
	  */
	def roomNumbers(implicit connection: Connection) = 
		pullColumn(model.roomNumberColumn).flatMap { value => value.string }
	
	/**
	  * creatorIds of the accessible StreetAddresses
	  */
	def creatorIds(implicit connection: Connection) = 
		pullColumn(model.creatorIdColumn).flatMap { value => value.int }
	
	/**
	  * createds of the accessible StreetAddresses
	  */
	def createds(implicit connection: Connection) = 
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = StreetAddressModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = StreetAddressFactory
	
	override protected def defaultOrdering = None
	
	override def filter(additionalCondition: Condition): ManyStreetAddressesAccess = 
		new ManyStreetAddressesAccess.ManyStreetAddressesSubView(this, additionalCondition)
	
	
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

