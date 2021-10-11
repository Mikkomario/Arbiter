package vf.arbiter.command.app

import utopia.citadel.database.access.many.user.DbManyUserSettings
import utopia.citadel.database.model.user.UserModel
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.metropolis.model.partial.user.UserSettingsData
import utopia.metropolis.model.stored.user.User
import utopia.vault.database.Connection

import scala.io.StdIn

/**
 * Contains interactive actions related to users
 * @author Mikko Hilpinen
 * @since 10.10.2021, v0.1
 */
object UserActions
{
	/**
	 * Registers a new user, if possible
	 * @param userName Name of the new user
	 * @param connection Implicit DB connection
	 * @return New user (may be None) + linked company (may be None)
	 */
	def register(userName: String)(implicit connection: Connection) =
		DbManyUserSettings.withName(userName).bestMatch(Vector(_.email.isEmpty)).headOption match
		{
			// Case: User already exists => may login as that user
			case Some(existingSettings) =>
				println("User with that name already exists")
				if (StdIn.ask("Do you want to resume as that user?"))
					Some(User(existingSettings.id, existingSettings)) -> None
				else
					None -> None
			// Case: User doesn't exist => creates one
			case None =>
				val user = UserModel.insert(UserSettingsData(userName))
				// Creates a new company for that user (or joins an existing company)
				println("What's the name of your company?")
				println("Hint: If you wish to join an existing company, you can write part of that company's name")
				val company = StdIn.readNonEmptyLine().flatMap { CompanyActions.startOrJoin(user.id, _) }
				Some(user) -> company
		}
}
