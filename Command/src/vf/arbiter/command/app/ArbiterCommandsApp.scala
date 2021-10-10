package vf.arbiter.command.app

import utopia.citadel.database.access.many.user.DbManyUserSettings
import utopia.citadel.database.model.user.UserModel
import utopia.citadel.util.CitadelContext
import utopia.flow.datastructure.mutable.PointerWithEvents
import utopia.flow.util.CollectionExtensions._
import vf.arbiter.core.util.Globals._
import utopia.flow.generic.DataType
import utopia.flow.util.console.{ArgumentSchema, Command}
import utopia.flow.util.console.ConsoleExtensions._
import utopia.metropolis.model.partial.user.UserSettingsData
import utopia.metropolis.model.stored.user.User
import vf.arbiter.core.model.stored.company.Company

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
	
	val userPointer = new PointerWithEvents[Option[User]](None)
	def user = userPointer.value
	def user_=(newUser: User) = userPointer.value = Some(newUser)
	userPointer.addListener { _.newValue.foreach { u => println(s"Welcome, ${u.settings.name}") } }
	
	val companyPointer = new PointerWithEvents[Option[Company]](None)
	def company = companyPointer.value
	def company_=(newCompany: Option[Company]) = companyPointer.value = newCompany
	def company_=(newCompany: Company) = companyPointer.value = Some(newCompany)
	companyPointer.addListener { _.newValue.foreach { c => println(s"Using company ${c.name}") } }
	userPointer.addAnyChangeListener { company = None }
	
	val registerUserCommand = Command("register", "newuser")(ArgumentSchema("name")) { args =>
		val name = args("name").stringOr {
			println("Please specify a user name")
			StdIn.readLineUntilNotEmpty()
		}
		// Checks whether name is reserved
		connectionPool { implicit connection =>
			DbManyUserSettings.withName(name).bestMatch(Vector(_.email.isEmpty)).headOption match
			{
				// Case: User already exists => may login as that user
				case Some(existingSettings) =>
					println("User with that name already exists")
					if (StdIn.ask("Do you want to resume as that user?"))
						user = User(existingSettings.id, existingSettings)
				// Case: User doesn't exist => creates one
				case None =>
					val u = UserModel.insert(UserSettingsData(name))
					user = u
					// Creates a new company for that user (or joins an existing company)
					StdIn.readNonEmptyLine("What's the name of your company?")
						.foreach { companyName =>
							company = CompanyActions.startOrJoin(u.id, companyName)
						}
			}
		}
	}
}
