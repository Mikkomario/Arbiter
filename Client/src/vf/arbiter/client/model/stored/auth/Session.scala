package vf.arbiter.client.model.stored.auth

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.client.database.access.single.auth.session.DbSingleSession
import vf.arbiter.client.model.partial.auth.SessionData

/**
  * Represents a session that has already been stored in the database
  * @param id id of this session in the database
  * @param data Wrapped session data
  * @author Mikko Hilpinen
  * @since 01.05.2023, v2.0
  */
case class Session(id: Int, data: SessionData) extends StoredModelConvertible[SessionData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this session in the database
	  */
	def access = DbSingleSession(id)
}

