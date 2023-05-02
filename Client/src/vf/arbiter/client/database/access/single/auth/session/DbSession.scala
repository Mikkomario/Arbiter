package vf.arbiter.client.database.access.single.auth.session

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NonDeprecatedView
import utopia.vault.sql.Condition
import vf.arbiter.client.database.factory.auth.SessionFactory
import vf.arbiter.client.database.model.auth.SessionModel
import vf.arbiter.client.model.stored.auth.Session

/**
  * Used for accessing individual sessions
  * @author Mikko Hilpinen
  * @since 01.05.2023, v2.0
  */
object DbSession extends SingleRowModelAccess[Session] with NonDeprecatedView[Session] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = SessionModel
	
	/**
	 * @return An access point to the currently active session, if applicable.
	 *         Assumes that only one session may be active at a time.
	 */
	def active = UniqueSessionAccess(model.nonDeprecatedCondition)
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SessionFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted session
	  * @return An access point to that session
	  */
	def apply(id: Int) = DbSingleSession(id)
	
	/**
	  * @param condition Filter condition to apply. Should yield unique sessions.
	  * @return An access point to the session that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueSessionAccess(mergeCondition(condition))
}

