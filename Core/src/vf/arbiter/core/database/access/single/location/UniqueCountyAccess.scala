package vf.arbiter.core.database.access.single.location

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
  * A common trait for access points that return individual and distinct Countys.
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait UniqueCountyAccess 
	extends SingleRowModelAccess[County] with DistinctModelAccess[County, Option[County], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * County name, 
		with that county's or country's primary language. None if no instance (or value) was found.
	  */
	def name(implicit connection: Connection) = pullColumn(model.nameColumn).int
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CountyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CountyFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the name of the targeted County instance(s)
	  * @param newName A new name to assign
	  * @return Whether any County instance was affected
	  */
	def name_=(newName: Int)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
}

