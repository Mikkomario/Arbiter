package vf.arbiter.core.database.access.many.location

import java.time.Instant
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, SubView}
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.location.CountyFactory
import vf.arbiter.core.database.model.location.CountyModel
import vf.arbiter.core.model.stored.location.County

object ManyCountiesAccess
{
	// NESTED	--------------------
	
	private class ManyCountiesSubView(override val parent: ManyRowModelAccess[County], 
		override val filterCondition: Condition) 
		extends ManyCountiesAccess with SubView
}

/**
  * A common trait for access points which target multiple Counties at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait ManyCountiesAccess extends ManyRowModelAccess[County] with Indexed with FilterableView[ManyCountiesAccess]
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
	protected def model = CountyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override def factory = CountyFactory
	
	override def filter(additionalCondition: Condition): ManyCountiesAccess = 
		new ManyCountiesAccess.ManyCountiesSubView(this, additionalCondition)
	
	
	// OTHER	--------------------
	
	/**
	 * @param names County names
	 * @return An access point to counties with those names (might not include all of them)
	 */
	def withAnyOfNames(names: Iterable[String]) = filter(model.nameColumn in names)
	
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
}

