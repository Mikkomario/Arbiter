package vf.arbiter.client.view.vc

import utopia.citadel.database.access.many.user.DbManyUserSettings
import utopia.citadel.database.access.single.user.DbUserSettings
import utopia.vault.database.Connection
import vf.arbiter.client.database.access.single.auth.session.DbSession

import scala.concurrent.Future

/**
 * A view-controller used for managing the login process
 * @author Mikko Hilpinen
 * @since 1.5.2023, v2.0
 */
object LoginVc
{
	// OTHER    ------------------------
	
	def login()(implicit connection: Connection) = {
		// Checks whether there's an active session already
		DbSession.active.pull match {
			// Case: Already logged in => Returns the active session
			case Some(session) => Future.successful(session)
			// Case: Nobody's logged in => Presents the login- or the register view
			case None =>
				val users = DbManyUserSettings.pull
				// Case: No users registered => Displays the registration view
				if (users.isEmpty)
					???
				// Case: Users available => Displays the login view
				else
					???
		}
	}
}
