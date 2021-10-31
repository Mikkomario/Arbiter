package vf.arbiter.command.app

import utopia.bunnymunch.jawn.JsonBunny
import utopia.citadel.database.Tables
import utopia.citadel.util.CitadelContext
import utopia.flow.async.CloseHook
import utopia.flow.datastructure.mutable.PointerWithEvents
import utopia.flow.generic.ValueConversions._
import utopia.flow.parse.JsonParser
import utopia.flow.time.TimeExtensions._
import utopia.flow.time.Today
import utopia.flow.util.console.{ArgumentSchema, Command, CommandArguments, Console}
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.FileExtensions._
import utopia.metropolis.model.cached.LanguageIds
import utopia.metropolis.model.stored.user.UserSettings
import utopia.trove.controller.LocalDatabase
import utopia.vault.database.Connection
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Throw
import vf.arbiter.command.controller.{ArbiterDbSetupListener, ExportData, ImportDescriptions}
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
	CitadelContext.setup(executionContext, connectionPool, "arbiter_db")
	ErrorHandling.defaultPrinciple = Throw
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
		LocalDatabase.setup("data/sql", "arbiter_db", "database_version",
			Tables("arbiter_db", "database_version"), Some(listener))
		if (listener.failed)
		{
			println("Shutting down the database and quitting...")
			LocalDatabase.shutDown() match {
				case Success(_) => println("Database shut down")
				case Failure(error) =>
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
			CloseHook.registerAsyncAction {
				println("Shutting down the database...")
				LocalDatabase.shutDownAsync().map {
					case Success(_) => println("Database shut down")
					case Failure(error) =>
						error.printStackTrace()
						println("Failed to shut down the local database")
				}
			}
		}
	}
	
	val userSettingsPointer = new PointerWithEvents[Option[UserSettings]](None)
	def userSettings = userSettingsPointer.value
	def userSettings_=(newUser: UserSettings) = userSettingsPointer.value = Some(newUser)
	userSettingsPointer.addListener { _.newValue.foreach { u => println(s"Welcome, ${u.name}") } }
	
	def loggedIn = userSettings.nonEmpty
	
	val languageIdsPointer = userSettingsPointer.lazyMap {
		case Some(user) => connectionPool { implicit c => UserActions.validLanguageIdListForUserWithId(user.userId) }
		case None => LanguageIds(Vector())
	}
	
	val companyPointer = new PointerWithEvents[Option[DetailedCompany]](None)
	def company = companyPointer.value
	def company_=(newCompany: Option[DetailedCompany]) = companyPointer.value = newCompany
	def company_=(newCompany: DetailedCompany) = companyPointer.value = Some(newCompany)
	companyPointer.addListener { _.newValue.foreach { c => println(s"Using company ${c.details.name}") } }
	userSettingsPointer.addAnyChangeListener { company = None }
	
	val loginCommand = Command("login", help = "Logs you in as a specific user, enabling other actions")(
		ArgumentSchema("username", "name", help = "Your username")) { args =>
		// If no argument was provided, asks the user
		args("name").string.orElse { StdIn.readNonEmptyLine("Who do you want to log in as?") }
			// Logs in
			.flatMap { name => connectionPool { implicit c => UserActions.loginAs(name) } }
			// Updates logged user and company afterwards
			.foreach { case (newUser, newCompany) =>
				userSettings = newUser
				company = newCompany
				
				// Prompts company selection next
				if (newCompany.isEmpty)
					connectionPool { implicit c => CompanyActions.selectOneFromOwn(newUser.userId) }
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
					if (userSettings.forall { u => StdIn.ask(s"You're already logged in as ${
						u.name}. Do you still want to register a new user?") })
					{
						val (newUser, newCompany) = UserActions.register(name)
						newUser.foreach { userSettings = _ }
						company = newCompany
					}
				case "company" =>
					userSettings match
					{
						case Some(user) =>
							implicit val languageIds: LanguageIds = languageIdsPointer.value
							CompanyActions.startOrSelectFromOwn(user.userId, name).foreach { company = _ }
						case None => println("You must be logged in to register a new company")
					}
				case "customer" =>
					userSettings match
					{
						case Some(user) =>
							CompanyActions.findOrCreateOne(user.userId, name)
								.foreach { c => println(
									s"${c.nameAndYCode} is now registered and can be used as a customer") }
						case None => println("You should be logged in to register a new customer")
					}
				case other => println(s"Unrecognized target $other. Available options are: user | company | customer")
			}
		}
	}
	val backupCommand = Command("backup", help = "Exports all available data to a json document")(
		ArgumentSchema("path", "to", help = "Path to the json document to generate (optional)")) { args =>
		val path = args("path").stringOr {
			val defaultPath = s"backup/dump-${Today.toString}.json"
			StdIn.readNonEmptyLine(
				s"Please specify a relative or absolute path for the generated json file. Default = $defaultPath") match
			{
				case Some(answer) => if (answer.contains('.')) answer else answer + ".json"
				case None => defaultPath
			}
		}
		println(s"Exporting data to $path...")
		connectionPool { implicit c =>
			ExportData.toJson(path) match {
				case Success(_) => println("Data successfully written!")
				case Failure(error) =>
					error.printStackTrace()
					println(s"Data writing failed due to an error: ${error.getMessage}")
			}
		}
	}
	
	def selectCompanyCommand(userId: Int) = Command.withoutArguments("use",
		help = "Switches between owned companies") {
		connectionPool { implicit c => CompanyActions.selectOneFromOwn(userId) }.foreach { company = _ }
	}
	def claimCompanyCommand(userId: Int) = Command("claim", help = "Claims an existing company as your own company")(
		ArgumentSchema("company", help = "Name of the company to claim (or part of that company's name)")) { args =>
		args("company").string.orElse(StdIn.readNonEmptyLine(
			"What's the name of the company you want to claim? (part of company name is enough)"))
			.foreach { companyName =>
				connectionPool { implicit c =>
					implicit val languageIds: LanguageIds = languageIdsPointer.value
					CompanyActions.findAndJoinOne(userId, companyName)
				}
			}
	}
	def printInvoiceCommand(userId: Int, companyId: Int) = Command.withoutArguments("print",
		help = "Prints an invoice") { connectionPool { implicit c => InvoiceActions.findAndPrint(userId, companyId) } }
	def editCommand(userId: Int, companyId: Int) = Command("edit", help = "Edits a company product")(
		ArgumentSchema("target", defaultValue = "product")) { args =>
		args("target").getString.toLowerCase match
		{
			case "product" =>
				connectionPool { implicit c =>
					implicit val languageIds: LanguageIds = languageIdsPointer.value
					CompanyActions.editProduct(userId, companyId)
				}
			case _ => println("Unrecognized target. Supported values are: product")
		}
	}
	def createInvoiceCommand(userId: Int, senderCompany: DetailedCompany) =
		Command.withoutArguments("invoice", "send", "Creates a new invoice") {
			connectionPool { implicit connection =>
				implicit val languageIds: LanguageIds = languageIdsPointer.value
				InvoiceActions.create(userId, senderCompany)
			}
		}
	
	// Updates the available commands when the user logs in / selects a company
	val commandsPointer = userSettingsPointer.lazyMergeWith(companyPointer) { (user, company) =>
		val statefulCommands = user match
		{
			case Some(user) =>
				Vector(selectCompanyCommand(user.userId), claimCompanyCommand(user.userId)) ++
					company.toVector.flatMap { company => Vector(
						createInvoiceCommand(user.userId, company),
						printInvoiceCommand(user.userId, company.id),
						editCommand(user.userId, company.id)
					)}
			case None => Vector(loginCommand)
		}
		Vector(registerCommand, backupCommand) ++ statefulCommands
	}
	
	// Starts the console
	println("Welcome to Arbiter")
	println("Instructions: use 'help' or 'man' commands to get more information. Use exit to quit.")
	Console(commandsPointer, "Please enter the next command", closeCommandName = "exit").run()
	println("Bye!")
	
	System.exit(0)
}
