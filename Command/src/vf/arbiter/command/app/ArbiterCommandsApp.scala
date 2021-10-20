package vf.arbiter.command.app

import utopia.bunnymunch.jawn.JsonBunny
import utopia.citadel.database.Tables
import utopia.citadel.util.CitadelContext
import utopia.flow.async.CloseHook
import utopia.flow.datastructure.mutable.PointerWithEvents
import utopia.flow.generic.ValueConversions._
import utopia.flow.generic.DataType
import utopia.flow.parse.JsonParser
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.console.{ArgumentSchema, Command, CommandArguments, Console}
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.FileExtensions._
import utopia.metropolis.model.cached.LanguageIds
import utopia.metropolis.model.stored.user.User
import utopia.trove.controller.LocalDatabase
import utopia.vault.database.Connection
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Throw
import vf.arbiter.command.controller.{ArbiterDbSetupListener, ImportDescriptions}
import vf.arbiter.core.model.combined.company.DetailedCompany
import vf.arbiter.core.util.Globals._

import scala.io.StdIn
import scala.util.{Failure, Success}

/**
 * A command line application for the Arbiter project
 * @author Mikko Hilpinen
 * @since 10.10.2021, v0.1
 */
object ArbiterCommandsApp extends App
{
	DataType.setup()
	implicit val jsonParser: JsonParser = JsonBunny
	
	val startArguments = CommandArguments(Vector(
		ArgumentSchema("connect", "C", false, "Whether to use a local MariaDB database instead of Trove"),
		ArgumentSchema("user", "u", "root", "User used when connecting to a local MariaDB Database"),
		ArgumentSchema("password", "pw", "Password used when connecting to a local MariaDB Database")
	), args.toVector)
	
	// Sets up database access
	if (startArguments("connect").getBoolean)
	{
		Connection.modifySettings { _.copy(user = startArguments("user").getString,
			password = startArguments("password").getString) }
	}
	else
	{
		val listener = new ArbiterDbSetupListener()
		println("Configuring the database...")
		LocalDatabase.setup("sql", "arbiter_db", "database_version",
			Tables("arbiter_db", "database_version"), Some(listener))
		if (listener.failed)
		{
			println("Shutting down the database and quitting...")
			LocalDatabase.shutDown().failure.foreach { error =>
				error.printStackTrace()
				println(s"Failure while shutting down the database: ${error.getMessage}")
			}
			System.exit(1)
		}
		else
		{
			// Performs description updates
			println("Updating description data...")
			connectionPool.tryWith { implicit c =>
				if (listener.updated)
					ImportDescriptions.all()
				else
					ImportDescriptions.ifModified()
			}.flatten match
			{
				case Success(_) => println("Descriptions imported successfully!")
				case Failure(error) =>
					error.printStackTrace()
					println(s"Description importing failed due to an error: ${error.getMessage}")
					println("Program continues but there may be some data-related problems")
			}
			
			// Shuts down the database when jvm is being closed
			CloseHook.maxShutdownTime = 20.seconds
			CloseHook.registerAsyncAction { LocalDatabase.shutDownAsync().map {
				case Success(_) => ()
				case Failure(error) =>
					error.printStackTrace()
					println("Failed to shut down the local database")
			} }
		}
	}
	
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
	def printInvoiceCommand(userId: Int, companyId: Int) = Command.withoutArguments("print",
		help = "Prints an invoice") { connectionPool { implicit c => InvoiceActions.findAndPrint(userId, companyId) } }
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
					company.toVector.flatMap { company =>
						Vector(createInvoiceCommand(user.id, company), printInvoiceCommand(user.id, company.id))
					}
			case None => Vector(loginCommand)
		}
		Vector(registerCommand) ++ statefulCommands
	}
	
	// Starts the console
	println("Welcome to Arbiter")
	println("Instructions: use 'help' or 'man' commands to get more information. Use exit to quit.")
	Console(commandsPointer, "Please enter the next command", closeCommandName = "exit").run()
	println("Bye!")
	
	System.exit(0)
}
