package vf.arbiter.command.database.access.single.environment

import java.time.Instant
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.command.database.factory.environment.DescriptionImportFactory
import vf.arbiter.command.database.model.environment.DescriptionImportModel
import vf.arbiter.command.model.stored.environment.DescriptionImport

/**
  * A common trait for access points that return individual and distinct DescriptionImports.
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
trait UniqueDescriptionImportAccess 
	extends SingleRowModelAccess[DescriptionImport] 
		with DistinctModelAccess[DescriptionImport, Option[DescriptionImport], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Path to the read file. None if no instance (or value) was found.
	  */
	def path(implicit connection: Connection) = pullColumn(model.pathColumn).string
	
	/**
	  * Time when this file was read. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = DescriptionImportModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DescriptionImportFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the created of the targeted DescriptionImport instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any DescriptionImport instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the path of the targeted DescriptionImport instance(s)
	  * @param newPath A new path to assign
	  * @return Whether any DescriptionImport instance was affected
	  */
	def path_=(newPath: String)(implicit connection: Connection) = putColumn(model.pathColumn, newPath)
}

