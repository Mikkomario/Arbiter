package vf.arbiter.core.database.access.many.location

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.location.CountyFactory
import vf.arbiter.core.database.model.location.CountyModel
import vf.arbiter.core.model.stored.location.County

import java.time.Instant

object ManyCountiesAccess extends ViewFactory[ManyCountiesAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyCountiesAccess = _ManyCountiesAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyCountiesAccess(override val accessCondition: Option[Condition]) 
		extends ManyCountiesAccess
}

/**
  * A common trait for access points which target multiple Counties at a time
  * @author Mikko Hilpinen
  * @since 31.10.2021
  */
trait ManyCountiesAccess 
	extends ManyRowModelAccess[County] with Indexed with FilterableView[ManyCountiesAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * names of the accessible Counties
	  */
	def names(implicit connection: Connection) = pullColumn(model.nameColumn)
		.flatMap { value => value.string }
	
	/**
	  * creatorIds of the accessible Counties
	  */
	def creatorIds(implicit connection: Connection) = 
		pullColumn(model.creatorIdColumn).flatMap { value => value.int }
	
	/**
	  * creationTimes of the accessible Counties
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	def model = CountyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CountyFactory
	
	override def self = this
	
	override def apply(condition: Condition): ManyCountiesAccess = ManyCountiesAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the created of the targeted County instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any County instance was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted County instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any County instance was affected
	  */
	def creatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the name of the targeted County instance(s)
	  * @param newName A new name to assign
	  * @return Whether any County instance was affected
	  */
	def names_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
	
	/**
	  * @param names County names
	  * @return An access point to counties with those names (might not include all of them)
	  */
	def withAnyOfNames(names: Iterable[String]) = filter(model.nameColumn in names)
}

