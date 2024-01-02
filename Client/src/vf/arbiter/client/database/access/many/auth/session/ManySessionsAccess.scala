package vf.arbiter.client.database.access.many.auth.session

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{ChronoRowFactoryView, NullDeprecatableView}
import utopia.vault.sql.Condition
import vf.arbiter.client.database.factory.auth.SessionFactory
import vf.arbiter.client.database.model.auth.SessionModel
import vf.arbiter.client.model.stored.auth.Session

import java.time.Instant

object ManySessionsAccess
{
	// NESTED	--------------------
	
	private class ManySessionsSubView(condition: Condition) extends ManySessionsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple sessions at a time
  * @author Mikko Hilpinen
  * @since 01.05.2023, v2.0
  */
trait ManySessionsAccess 
	extends ManyRowModelAccess[Session] with ChronoRowFactoryView[Session, ManySessionsAccess] 
		with NullDeprecatableView[ManySessionsAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * user ids of the accessible sessions
	  */
	def userIds(implicit connection: Connection) = pullColumn(model.userIdColumn).map { v => v.getInt }
	
	/**
	  * creation times of the accessible sessions
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	/**
	  * deprecation times of the accessible sessions
	  */
	def deprecationTimes(implicit connection: Connection) = 
		pullColumn(model.deprecatedAfterColumn).flatMap { v => v.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = SessionModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SessionFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManySessionsAccess = 
		new ManySessionsAccess.ManySessionsSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted sessions
	  * @param newCreated A new created to assign
	  * @return Whether any session was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the deprecation times of the targeted sessions
	  * @param newDeprecatedAfter A new deprecated after to assign
	  * @return Whether any session was affected
	  */
	def deprecationTimes_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.deprecatedAfterColumn, newDeprecatedAfter)
	
	/**
	  * Updates the user ids of the targeted sessions
	  * @param newUserId A new user id to assign
	  * @return Whether any session was affected
	  */
	def userIds_=(newUserId: Int)(implicit connection: Connection) = putColumn(model.userIdColumn, newUserId)
}

