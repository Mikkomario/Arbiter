package vf.arbiter.core.database.access.single.location

import java.time.Instant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.location.PostalCodeFactory
import vf.arbiter.core.database.model.location.PostalCodeModel
import vf.arbiter.core.model.stored.location.PostalCode

/**
  * A common trait for access points that return individual and distinct PostalCodes.
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
trait UniquePostalCodeAccess 
	extends SingleRowModelAccess[PostalCode] with DistinctModelAccess[PostalCode, Option[PostalCode], Value] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * The number portion of this postal code. None if no instance (or value) was found.
	  */
	def number(implicit connection: Connection) = pullColumn(model.numberColumn).string
	
	/**
	  * Id of the county where this postal code is resides. None if no instance (or value) was found.
	  */
	def countyId(implicit connection: Connection) = pullColumn(model.countyIdColumn).int
	
	/**
	  * Id of the user linked with this PostalCode. None if no instance (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this PostalCode was first created. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PostalCodeModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PostalCodeFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the countyId of the targeted PostalCode instance(s)
	  * @param newCountyId A new countyId to assign
	  * @return Whether any PostalCode instance was affected
	  */
	def countyId_=(newCountyId: Int)(implicit connection: Connection) = 
		putColumn(model.countyIdColumn, newCountyId)
	
	/**
	  * Updates the created of the targeted PostalCode instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any PostalCode instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted PostalCode instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any PostalCode instance was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the number of the targeted PostalCode instance(s)
	  * @param newNumber A new number to assign
	  * @return Whether any PostalCode instance was affected
	  */
	def number_=(newNumber: String)(implicit connection: Connection) = putColumn(model.numberColumn, 
		newNumber)
}

