package vf.arbiter.command.app

import utopia.citadel.database.access.many.DbUsers
import utopia.citadel.database.access.single.DbUser
import utopia.citadel.database.model.user.UserModel
import utopia.citadel.util.CitadelContext
import vf.arbiter.core.util.Globals._
import utopia.flow.generic.DataType
import utopia.flow.util.console.{ArgumentSchema, Command}
import utopia.flow.util.console.ConsoleExtensions._
import utopia.metropolis.model.partial.user.UserSettingsData
import utopia.metropolis.model.stored.user.User

import scala.io.StdIn

/**
 * A command line application for the Arbiter project
 * @author Mikko Hilpinen
 * @since 10.10.2021, v0.1
 */
object ArbiterCommandsApp extends App
{
	DataType.setup()
	CitadelContext.setup(executionContext, connectionPool, "arbiter_db")
	
	var user: Option[User] = None
	
	val registerUserCommand = Command("register", "newuser")(
		ArgumentSchema("name"), ArgumentSchema("company")) { args =>
		val name = args("name").stringOr {
			println("Please specify a user name")
			StdIn.readLineUntilNotEmpty()
		}
		// Checks whether name is reserved
		connectionPool { implicit connection =>
			if (DbUsers.existsUserWithName(name))
				println("User with that name already exists")
			else
			{
				// Inserts the user
				user = Some(UserModel.insert(UserSettingsData(name)))
			}
		}
	}
	
	// Checks whether there already exists a user
	connectionPool { implicit connection =>
		if (DbUsers.isEmpty)
		{
		
		}
	}
}
