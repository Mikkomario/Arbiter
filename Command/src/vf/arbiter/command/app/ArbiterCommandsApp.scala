package vf.arbiter.command.app

import utopia.citadel.util.CitadelContext
import utopia.flow.datastructure.mutable.PointerWithEvents
import utopia.flow.generic.ValueConversions._
import vf.arbiter.core.util.Globals._
import utopia.flow.generic.DataType
import utopia.flow.util.console.{ArgumentSchema, Command}
import utopia.flow.util.console.ConsoleExtensions._
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
	
	def loggedIn = user.nonEmpty
	
	val companyPointer = new PointerWithEvents[Option[Company]](None)
	def company = companyPointer.value
	def company_=(newCompany: Option[Company]) = companyPointer.value = newCompany
	def company_=(newCompany: Company) = companyPointer.value = Some(newCompany)
	companyPointer.addListener { _.newValue.foreach { c => println(s"Using company ${c.name}") } }
	userPointer.addAnyChangeListener { company = None }
	
	val registerCommand = Command("register", "new")(
		ArgumentSchema("target", defaultValue = "user"), ArgumentSchema("name")) { args =>
		
		val target = args("target").getString.toLowerCase
		lazy val name = args("name").stringOr {
			println(s"Please specify a $target name")
			StdIn.readLineUntilNotEmpty()
		}
		connectionPool { implicit connection =>
			target match
			{
				case "user" =>
					if (user.forall { u => StdIn.ask(s"You're already logged in as ${
						u.settings.name}. Do you still want to register a new user?") })
					{
						val (newUser, newCompany) = UserActions.register(name)
						newUser.foreach { user = _ }
						company = newCompany
					}
				case "company" =>
					user match
					{
						case Some(user) => CompanyActions.startOrSelectFromOwn(user.id, name).foreach { company = _ }
						case None => println("You must be logged in to register a new company")
					}
				case "customer" =>
					CompanyActions.findOrCreateOne(name)
						.foreach { c => println(s"${c.nameAndYCode} is now registered and can be used as a customer") }
				case other => println(s"Unrecognized target $other. Available options are: user | company | customer")
			}
		}
	}
}
