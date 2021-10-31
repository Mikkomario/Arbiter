package vf.arbiter.core.database.access.single.location

import java.time.Instant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.location.CountyFactory
import vf.arbiter.core.database.model.location.CountyModel
import vf.arbiter.core.model.stored.location.County

/**
  * A common trait for access points that return individual and distinct Counties.
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait UniqueCountyAccess 
	extends SingleRowModelAccess[County] with DistinctModelAccess[County, Option[County], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * County name, 
		with that county's or country's primary language. None if no instance (or value) was found.
	  */
	def name(implicit connection: Connection) = pullColumn(model.nameColumn).string
	
	/**
	  * Id of the user who registered this county. None if no instance (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this County was first created. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CountyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CountyFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the created of the targeted County instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any County instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted County instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any County instance was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the name of the targeted County instance(s)
	  * @param newName A new name to assign
	  * @return Whether any County instance was affected
	  */
	def name_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
}

