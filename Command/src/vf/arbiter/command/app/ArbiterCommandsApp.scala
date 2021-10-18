package vf.arbiter.command.app

import utopia.bunnymunch.jawn.JsonBunny
import utopia.citadel.util.CitadelContext
import utopia.flow.datastructure.mutable.PointerWithEvents
import utopia.flow.generic.ValueConversions._
import vf.arbiter.core.util.Globals._
import utopia.flow.generic.DataType
import utopia.flow.parse.JsonParser
import utopia.flow.util.console.{ArgumentSchema, Command, Console}
import utopia.flow.util.console.ConsoleExtensions._
import utopia.metropolis.model.cached.LanguageIds
import utopia.metropolis.model.stored.user.User
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Throw
import vf.arbiter.core.model.combined.company.DetailedCompany

import scala.io.StdIn

/**
 * A command line application for the Arbiter project
 * @author Mikko Hilpinen
 * @since 10.10.2021, v0.1
 */
object ArbiterCommandsApp extends App
{
	DataType.setup()
	implicit val jsonParser: JsonParser = JsonBunny
	CitadelContext.setup(executionContext, connectionPool, "arbiter_db")
	ErrorHandling.defaultPrinciple = Throw
	
	val userPointer = new PointerWithEvents[Option[User]](None)
	def user = userPointer.value
	def user_=(newUser: User) = userPointer.value = Some(newUser)
	userPointer.addListener { _.newValue.foreach { u => println(s"Welcome, ${u.settings.name}") } }
	
	def loggedIn = user.nonEmpty
	
	val languageIdsPointer = userPointer.lazyMap {
		case Some(user) => connectionPool { implicit c => UserActions.validLanguageIdListForUserWithId(user.id) }
		case None => LanguageIds(Vector())
	}
	
	val companyPointer = new PointerWithEvents[Option[DetailedCompany]](None)
	def company = companyPointer.value
	def company_=(newCompany: Option[DetailedCompany]) = companyPointer.value = newCompany
	def company_=(newCompany: DetailedCompany) = companyPointer.value = Some(newCompany)
	companyPointer.addListener { _.newValue.foreach { c => println(s"Using company ${c.details.name}") } }
	userPointer.addAnyChangeListener { company = None }
	
	val loginCommand = Command("login", help = "Logs you in as a specific user, enabling other actions")(
		ArgumentSchema("username", "name", help = "Your username")) { args =>
		// If no argument was provided, asks the user
		args("name").string.orElse { StdIn.readNonEmptyLine("Who do you want to log in as?") }
			// Logs in
			.flatMap { name => connectionPool { implicit c => UserActions.loginAs(name) } }
			// Updates logged user and company afterwards
			.foreach { case (newUser, newCompany) =>
				user = newUser
				company = newCompany
				
				// Prompts company selection next
				if (newCompany.isEmpty)
					connectionPool { implicit c => CompanyActions.selectOneFromOwn(newUser.id) }
						.foreach { company = _ }
			}
	}
	val registerCommand = Command("register", "new", "Registers a new user, company or customer")(
		ArgumentSchema("target", defaultValue = "user",
			help = "Type of entity being created (user | company | customer)"),
		ArgumentSchema("name", help = "Name of the entity that's being created")) { args =>
		
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
						case Some(user) =>
							implicit val languageIds: LanguageIds = languageIdsPointer.value
							CompanyActions.startOrSelectFromOwn(user.id, name).foreach { company = _ }
						case None => println("You must be logged in to register a new company")
					}
				case "customer" =>
					user match
					{
						case Some(user) =>
							CompanyActions.findOrCreateOne(user.id, name)
								.foreach { c => println(
									s"${c.nameAndYCode} is now registered and can be used as a customer") }
						case None => println("You should be logged in to register a new customer")
					}
				case other => println(s"Unrecognized target $other. Available options are: user | company | customer")
			}
		}
	}
	
	def selectCompanyCommand(userId: Int) = Command.withoutArguments("use",
		help = "Switches between owned companies") {
		connectionPool { implicit c => CompanyActions.selectOneFromOwn(userId) }.foreach { company = _ }
	}
	def printInvoiceCommand(companyId: Int) = Command.withoutArguments("print", help = "Prints an invoice") {
		connectionPool { implicit c => InvoiceActions.findAndPrint(companyId) }
	}
	def createInvoiceCommand(userId: Int, senderCompany: DetailedCompany) =
		Command.withoutArguments("invoice", "send", "Creates a new invoice") {
			connectionPool { implicit connection =>
				implicit val languageIds: LanguageIds = languageIdsPointer.value
				InvoiceActions.create(userId, senderCompany)
			}
		}
	
	// Updates the available commands when the user logs in / selects a company
	val commandsPointer = userPointer.lazyMergeWith(companyPointer) { (user, company) =>
		val statefulCommands = user match
		{
			case Some(user) =>
				Vector(selectCompanyCommand(user.id)) ++
					company.map { company => createInvoiceCommand(user.id, company) }
			case None => Vector(loginCommand)
		}
		val companyStatefulCommands = company.map { company => printInvoiceCommand(company.id) }
		Vector(registerCommand) ++ statefulCommands ++ companyStatefulCommands
	}
	
	// Starts the console
	println("Welcome to Arbiter")
	println("Instructions: use 'help' or 'man' commands to get more information. Use exit to quit.")
	Console(commandsPointer, "Please enter the next command", closeCommandName = "exit").run()
	println("Bye!")
}
