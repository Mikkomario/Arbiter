package vf.arbiter.client.database.model.auth

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import utopia.vault.nosql.storable.deprecation.DeprecatableAfter
import vf.arbiter.client.database.factory.auth.SessionFactory
import vf.arbiter.client.model.partial.auth.SessionData
import vf.arbiter.client.model.stored.auth.Session

import java.time.Instant

/**
  * Used for constructing SessionModel instances and for inserting sessions to the database
  * @author Mikko Hilpinen
  * @since 01.05.2023, v2.0
  */
object SessionModel 
	extends DataInserter[SessionModel, Session, SessionData] with DeprecatableAfter[SessionModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains session user id
	  */
	val userIdAttName = "userId"
	
	/**
	  * Name of the property that contains session created
	  */
	val createdAttName = "created"
	
	/**
	  * Name of the property that contains session deprecated after
	  */
	val deprecatedAfterAttName = "deprecatedAfter"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains session user id
	  */
	def userIdColumn = table(userIdAttName)
	
	/**
	  * Column that contains session created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains session deprecated after
	  */
	def deprecatedAfterColumn = table(deprecatedAfterAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = SessionFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: SessionData) = 
		apply(None, Some(data.userId), Some(data.created), data.deprecatedAfter)
	
	override protected def complete(id: Value, data: SessionData) = Session(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this session was opened
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param deprecatedAfter Time when this session was closed
	  * @return A model containing only the specified deprecated after
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = Some(deprecatedAfter))
	
	/**
	  * @param id A session id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param userId Id of the logged-in user
	  * @return A model containing only the specified user id
	  */
	def withUserId(userId: Int) = apply(userId = Some(userId))
}

/**
  * Used for interacting with Sessions in the database
  * @param id session database id
  * @author Mikko Hilpinen
  * @since 01.05.2023, v2.0
  */
case class SessionModel(id: Option[Int] = None, userId: Option[Int] = None, created: Option[Instant] = None, 
	deprecatedAfter: Option[Instant] = None) 
	extends StorableWithFactory[Session]
{
	// IMPLEMENTED	--------------------
	
	override def factory = SessionModel.factory
	
	override def valueProperties = {
		import SessionModel._
		Vector("id" -> id, userIdAttName -> userId, createdAttName -> created, 
			deprecatedAfterAttName -> deprecatedAfter)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this session was opened
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param deprecatedAfter Time when this session was closed
	  * @return A new copy of this model with the specified deprecated after
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	/**
	  * @param userId Id of the logged-in user
	  * @return A new copy of this model with the specified user id
	  */
	def withUserId(userId: Int) = copy(userId = Some(userId))
}

