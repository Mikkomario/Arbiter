package vf.arbiter.client.database.access.many.auth.session

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{NonDeprecatedView, UnconditionalView}
import vf.arbiter.client.model.stored.auth.Session

/**
  * The root access point when targeting multiple sessions at a time
  * @author Mikko Hilpinen
  * @since 01.05.2023, v2.0
  */
object DbSessions extends ManySessionsAccess with NonDeprecatedView[Session]
{
	// COMPUTED	--------------------
	
	/**
	  * A copy of this access point that includes historical (i.e. deprecated) sessions
	  */
	def includingHistory = DbSessionsIncludingHistory
	
	
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted sessions
	  * @return An access point to sessions with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbSessionsSubset(ids)
	
	
	// NESTED	--------------------
	
	object DbSessionsIncludingHistory extends ManySessionsAccess with UnconditionalView
	
	class DbSessionsSubset(targetIds: Set[Int]) extends ManySessionsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

