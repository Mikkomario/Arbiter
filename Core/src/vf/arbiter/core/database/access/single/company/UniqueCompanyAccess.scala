package vf.arbiter.core.database.access.single.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.company.CompanyFactory
import vf.arbiter.core.database.model.company.CompanyModel
import vf.arbiter.core.model.stored.company.Company

/**
  * A common trait for access points that return individual and distinct Companies.
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait UniqueCompanyAccess 
	extends SingleRowModelAccess[Company] with DistinctModelAccess[Company, Option[Company], Value] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Official registration code of this company (id in the country system). None if no instance (or value) was found.
	  */
	def yCode(implicit connection: Connection) = pullColumn(model.yCodeColumn).string
	
	/**
	  * Id of the user who registered this company to this system. None if no instance (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this company was registered. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the created of the targeted Company instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any Company instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted Company instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any Company instance was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the yCode of the targeted Company instance(s)
	  * @param newYCode A new yCode to assign
	  * @return Whether any Company instance was affected
	  */
	def yCode_=(newYCode: String)(implicit connection: Connection) = putColumn(model.yCodeColumn, newYCode)
}

