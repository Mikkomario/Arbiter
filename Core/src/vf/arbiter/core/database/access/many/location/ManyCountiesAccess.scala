package vf.arbiter.core.database.access.many.location

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.location.CountyFactory
import vf.arbiter.core.database.model.location.CountyModel
import vf.arbiter.core.model.stored.location.County

/**
  * A common trait for access points which target multiple Counties at a time
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait ManyCountiesAccess extends ManyRowModelAccess[County] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * names of the accessible Counties
	  */
	def names(implicit connection: Connection) = pullColumn(model.nameColumn).flatMap { value => value.int }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CountyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CountyFactory
	
	override protected def defaultOrdering = None
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the name of the targeted County instance(s)
	  * @param newName A new name to assign
	  * @return Whether any County instance was affected
	  */
	def name_=(newName: Int)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
}

