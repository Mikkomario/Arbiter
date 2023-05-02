package vf.arbiter.client.database.access.single.auth.session

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.client.model.stored.auth.Session

/**
  * An access point to individual sessions, based on their id
  * @author Mikko Hilpinen
  * @since 01.05.2023, v2.0
  */
case class DbSingleSession(id: Int) extends UniqueSessionAccess with SingleIntIdModelAccess[Session]

