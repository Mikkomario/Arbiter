package vf.arbiter.client.database.access.single.auth.session

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleChronoRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NullDeprecatableView
import utopia.vault.sql.Condition
import vf.arbiter.client.database.factory.auth.SessionFactory
import vf.arbiter.client.database.model.auth.SessionModel
import vf.arbiter.client.model.stored.auth.Session

import java.time.Instant

object UniqueSessionAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueSessionAccess = new _UniqueSessionAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueSessionAccess(condition: Condition) extends UniqueSessionAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct sessions.
  * @author Mikko Hilpinen
  * @since 01.05.2023, v2.0
  */
trait UniqueSessionAccess 
	extends SingleChronoRowModelAccess[Session, UniqueSessionAccess] 
		with NullDeprecatableView[UniqueSessionAccess] 
		with DistinctModelAccess[Session, Option[Session], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the logged-in user. None if no session (or value) was found.
	  */
	def userId(implicit connection: Connection) = pullColumn(model.userIdColumn).int
	
	/**
	  * Time when this session was opened. None if no session (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Time when this session was closed. None if no session (or value) was found.
	  */
	def deprecatedAfter(implicit connection: Connection) = pullColumn(model.deprecatedAfterColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = SessionModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SessionFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueSessionAccess = 
		new UniqueSessionAccess._UniqueSessionAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted sessions
	  * @param newCreated A new created to assign
	  * @return Whether any session was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the deprecation times of the targeted sessions
	  * @param newDeprecatedAfter A new deprecated after to assign
	  * @return Whether any session was affected
	  */
	def deprecatedAfter_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.deprecatedAfterColumn, newDeprecatedAfter)
	
	/**
	  * Updates the user ids of the targeted sessions
	  * @param newUserId A new user id to assign
	  * @return Whether any session was affected
	  */
	def userId_=(newUserId: Int)(implicit connection: Connection) = putColumn(model.userIdColumn, newUserId)
}

