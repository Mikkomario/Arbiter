package vf.arbiter.command.app

import utopia.access.http.Status
import utopia.bunnymunch.jawn.JsonBunny
import utopia.citadel.database.Tables
import utopia.citadel.util.CitadelContext
import utopia.flow.async.context.CloseHook
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.range.Span
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.json.JsonParser
import utopia.flow.time.TimeExtensions._
import utopia.flow.time.Today
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.console.{ArgumentSchema, Command, CommandArguments, Console}
import utopia.flow.view.mutable.Pointer
import utopia.flow.view.mutable.eventful.EventfulPointer
import utopia.metropolis.model.cached.LanguageIds
import utopia.metropolis.model.stored.user.UserSettings
import utopia.trove.controller.LocalDatabase
import utopia.vault.database.Connection
import utopia.vault.database.columnlength.ColumnLengthRules
import utopia.vault.sql.{Count, Limit, SelectAll}
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Throw
import vf.arbiter.command.controller._
import vf.arbiter.core.model.combined.company.DetailedCompany
import vf.arbiter.core.model.stored.company.CompanyDetails
import vf.arbiter.core.util.Common._

import java.nio.file.{Path, Paths}
import java.time.{Instant, Month, Year}
import scala.concurrent.duration.Duration
import scala.io.StdIn
import scala.util.{Failure, Success}

/**
 * A command line application for the Arbiter project
 * @author Mikko Hilpinen
 * @since 10.10.2021, v0.1
 */
object ArbiterCommandsApp extends App
{
	// Sets up the program environment
	Status.setup()
	CitadelContext.setup(executionContext, connectionPool, "arbiter_db")
	ErrorHandling.defaultPrinciple = Throw
	implicit val jsonParser: JsonParser = JsonBunny
	
	private val closeConsoleFlag = Pointer(false)
	
	// Processes program startup arguments
	private val startArguments = CommandArguments(Vector(
		ArgumentSchema("connect", "C", false, "Whether to use a local MariaDB database instead of Trove"),
		ArgumentSchema("user", "u", "root", "User used when connecting to a local MariaDB Database"),
		ArgumentSchema("password", "pw", "Password used when connecting to a local MariaDB Database")
	), args.toVector)
	
	// Sets up database access
	// Case: Connecting to a custom database
	if (startArguments("connect").getBoolean) {
		Connection.modifySettings { _.copy(user = startArguments("user").getString,
			password = startArguments("password").getString, charsetName = "utf8",
			charsetCollationName = "utf8_general_ci") }
		
		// FIXME: Length rules and descriptions are not initialized
	}
	// Case: Connecting to a self-hosted database
	else {
		val listener = new ArbiterDbSetupListener()
		println("Configuring the database...")
		LocalDatabase.setup("data/sql", "arbiter_db", "database_version",
			Tables("arbiter_db", "database_version"), Some(listener), Some("utf8"), Some("utf8_general_ci"))
		if (listener.failed) {
			println("Shutting down the database and quitting...")
			LocalDatabase.shutDown() match {
				case Success(_) => println("Database shut down")
				case Failure(error) =>
					error.printStackTrace()
					println(s"Failure while shutting down the database: ${error.getMessage}")
			}
			System.exit(1)
		}
		else {
			// Applies length rules
			Paths.get("data/length-rules")
				.iterateChildren { _.filter { _.fileType == "json" }
					.foreach { rules => ColumnLengthRules.loadFrom(rules, CitadelContext.databaseName) } }
			
			// Performs description updates
			println("Updating description data...")
			connectionPool.tryWith { implicit c =>
				if (listener.updated)
					ImportDescriptions.all()
				else
					ImportDescriptions.ifModified()
			}.flatten match {
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
	
	// Sets up the user and company tracking
	private val userSettingsPointer = new EventfulPointer[Option[UserSettings]](None)
	private def userSettings = userSettingsPointer.value
	private def userSettings_=(newUser: UserSettings) = userSettingsPointer.value = Some(newUser)
	userSettingsPointer.addContinuousListener { _.newValue.foreach { u => println(s"Welcome, ${u.name}") } }
	
	private val languageIdsPointer = userSettingsPointer.lazyMap {
		case Some(user) => connectionPool { implicit c => UserActions.validLanguageIdListForUserWithId(user.userId) }
		case None => LanguageIds(Vector())
	}
	
	private val companyPointer = new EventfulPointer[Option[DetailedCompany]](None)
	def company = companyPointer.value
	def company_=(newCompany: Option[DetailedCompany]) = companyPointer.value = newCompany
	def company_=(newCompany: DetailedCompany) = companyPointer.value = Some(newCompany)
	companyPointer.addContinuousListener { _.newValue.foreach { c => println(s"Using company ${c.details.name}") } }
	userSettingsPointer.addAnyChangeListener { company = None }
	
	// TODO: Move specific commands, such as invoicing commands to separate files
	
	// Creates the basic commands
	private val loginCommand = Command("login", help = "Logs you in as a specific user, enabling other actions")(
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
	private val registerCommand = Command("register", "new", "Registers a new user, company or customer")(
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
	// Creates additional commands
	private val backupCommand = Command("backup", help = "Exports all available data to a json document")(
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
	private val importCommand = Command("import", help = "Imports previously dumped data back to this application")(
		ArgumentSchema("path", "from", help = "Path to the data json file")) { args =>
		val path = args("path").string match {
			case Some(argPath) => Some(argPath: Path)
			case None =>
				val defaultPath = ("backup": Path).iterateChildren { _.filter { _.fileType == "json" }
					.maxByOption { _.lastModified.getOrElse(Instant.EPOCH) } }.toOption.flatten
				val defaultStr = defaultPath match {
					case Some(path) => s" (default = $path (latest dump))"
					case None => ""
				}
				println("Please specify the path to the json data file you want to import" + defaultStr)
				if (defaultPath.isEmpty)
					println(s"Hint: Current directory is ${"".toAbsolutePath}")
				StdIn.readNonEmptyLine(retryPrompt = if (defaultPath.isEmpty)
					"Are you sure? Leaving this value will cancel this import." else "") match
				{
					case Some(input) => Some(input: Path)
					case None => defaultPath
				}
		}
		path.foreach { path =>
			if (path.notExists)
				println(s"Path ${path.toAbsolutePath} doesn't exist in the file system")
			else {
				connectionPool.tryWith { implicit connection =>
					ImportData.fromJson(path) match {
						case Success(_) => println("Data import completed")
						case Failure(error) =>
							error.printStackTrace()
							println(s"Data json parsing failed: ${error.getMessage}")
					}
				}.failure.foreach { error =>
					error.printStackTrace()
					println(s"Data import process failed unexpectedly: ${error.getMessage}")
				}
			}
		}
	}
	private val debugCommand = Command("debug", help = "Command that prints debugging information")(
		ArgumentSchema("table", help = "Name of the table you want to debug")) { args =>
		args("table").string match {
			case Some(tableName) =>
				connectionPool { implicit c =>
					if (c.existsTable("arbiter_db", tableName)) {
						val table = Tables(tableName)
						println(table)
						table.columns.foreach { col => println(s"- ${col.name} ${col.dataType}${
							if (col.allowsNull) "" else " not null"}") }
						println()
						val tableSize = c(Count(table)).firstValue.getInt
						if (tableSize > 0) {
							println(s"$tableSize rows")
							c(SelectAll(table) + Limit(3)).rows.foreach { row => println(s"- ${row.toModel}") }
						}
						else
							println("This table is empty")
					}
					else
						println(s"Table '$tableName' doesn't exist")
				}
			case None => println("Required argument 'table' is missing")
		}
	}
	private val clearAllCommand = Command("clear", help = "Clears all existing data")() { _ =>
		if (StdIn.ask("Are you sure you want to delete all existing data and close this program?")) {
			connectionPool { implicit c => c.dropDatabase("arbiter_db") }
			closeConsoleFlag.value = true
			println("Database cleared. Closing this program...")
		}
	}
	
	// Creates user-dependent commands
	private def selectCompanyCommand(userId: Int) = Command.withoutArguments("use",
		help = "Switches between owned companies") {
		connectionPool { implicit c => CompanyActions.selectOneFromOwn(userId) }.foreach { company = _ }
	}
	private def claimCompanyCommand(userId: Int) = Command("claim", help = "Claims an existing company as your own company")(
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
	// Creates company-dependent commands
	private def seeCommand(companyId: Option[Int]) = Command("see", "show",
		help = "Lists information concerning a company or an invoice")(
		ArgumentSchema("target", "t", "company", help = "The targeted item type: company | invoice"),
		ArgumentSchema("filter", "f", help = "Targeted item, such as company name part (optional)")) { args =>
		connectionPool { implicit c =>
			args("target").getString.toLowerCase.take(3) match {
				case "com" => CompanyActions.findAndDescribeOne(args("filter").getString)
				case "inv" =>
					companyId match {
						case Some(senderCompanyId) =>
							InvoiceActions.findAndDescribe(senderCompanyId, args("filter").getString)
						case None => println("Please select your own company first")
					}
				case o => println(s"Unrecognized target $o, please specify either 'company' or 'invoice'")
			}
		}
	}
	// TODO: Add support for other data types
	private def listCommand(companyId: Int) = Command("list", "l", help = "Lists data")(
		ArgumentSchema("target", "t", "invoice", "The targeted item group (currently only 'invoice' is supported)"),
		ArgumentSchema("filter", "f",
			help = "Additional filter to apply. In this case (partial) recipient company name. Optional."),
		ArgumentSchema("period", "time",
			help = "Time period to look back: day | week | month | quarter | year | decade | all. Default is context dependent.")) { args =>
		// Determines the time period to include
		val filter = args("filter").getString
		val duration: Duration = args("period").getString.toLowerCase match {
			case "day" => 1.days
			case "week" => 7.days
			case "month" => 30.days
			case "quarter" => 90.days
			case "year" => 365.days
			case "decade" => 3652.days
			case "all" => Duration.Inf
			case _ => if (filter.isEmpty) 30.days else 90.days
		}
		// Lists invoices
		implicit val languageIds: LanguageIds = languageIdsPointer.value
		connectionPool { implicit c => InvoiceActions.listLatest(companyId, filter, duration) }
	}
	private def cancelInvoiceCommand(companyId: Int) = Command("cancel", "delete",
		help = "Cancels an existing invoice")(
		ArgumentSchema("target", "filter",
			help = "Invoice index, reference number or company name (partial and optional)")) { args =>
			connectionPool { implicit c => InvoiceActions.findAndCancel(companyId, args("target").getString) }
		}
	private def printInvoiceCommand(userId: Int, companyId: Int) = Command("print", help = "Prints an invoice")(
		ArgumentSchema("target", "filter",
			help = "Invoice id, reference number or company name (partial and optional)")) { args =>
		connectionPool { implicit c => InvoiceActions.findAndPrint(userId, companyId, args("target").getString) }
	}
	private def editCommand(userId: Int, companyId: Option[Int]) = Command("edit",
		help = "Edits company/product/invoice information")(
		ArgumentSchema("target", defaultValue = "invoice",
			help = "Type of item targeted with this edit. Valid values are: 'invoice', 'company' and 'product'."),
		ArgumentSchema("filter", "f", help = "Filter applied in order to find the targeted item")) { args =>
		args("target").getString.toLowerCase match {
			case "invoice" =>
				companyId match {
					case Some(companyId) =>
						connectionPool { implicit c =>
							implicit val languageIds: LanguageIds = languageIdsPointer.value
							InvoiceActions.findAndEdit(userId, companyId, args("filter").getString)
						}
					case None => println("Please select a company first")
				}
			case "company" =>
				println("Which company do you want to edit?")
				StdIn.readNonEmptyLine("Instruction: Write full or partial company name",
					"Empty will cancel edit. Ok?")
					.foreach { companyName =>
						connectionPool { implicit c =>
							// Finds and modifies a company
							CompanyActions.edit(userId, companyName).foreach { newDetails =>
								// If the currently selected company was targeted, updates the applicable pointer, also
								companyPointer.mapCurrent { company =>
									if (company.id == newDetails.companyId)
										company.copy(details = newDetails)
									else
										company
								}
							}
						}
					}
			case "product" =>
				companyId match {
					case Some(companyId) =>
						connectionPool { implicit c =>
							implicit val languageIds: LanguageIds = languageIdsPointer.value
							CompanyActions.editProduct(userId, companyId)
						}
					case None => println("Please select a company first")
				}
			case _ => println("Unrecognized target. Supported values are: product")
		}
	}
	private def exportDataCommand(senderCompanyDetails: CompanyDetails) = Command("export", "summary",
		help = "Generates summary csv documents from company invoice data")(
		ArgumentSchema("year", defaultValue = Today.year.getValue, help = "Year from which data is collected"),
		ArgumentSchema("first", defaultValue = 1, help = "The first month to include in the summary [1,12]"),
		ArgumentSchema("last", defaultValue = Today.month.value, help = "The last month to include in the summary [1,12]"),
		ArgumentSchema("directory", "to", help = "Directory where reports will be generated (optional)")) { args =>
		val year = Year.of(args("year").getInt)
		val months = Span.numeric(args("first").getInt, args("last").getInt)
			.overlapWith(Span.numeric(1, 12))
			.getOrElse {
				println("The specified range of months was not within the allowed limit [1,12]. Uses the default range.")
				Span(1, Today.month.value)
			}
			.map(Month.of)
		val path = args("to").string match {
			case Some(str) => str: Path
			case None =>
				val defaultPath = ("summaries": Path)/senderCompanyDetails.name.replace(' ', '-')/
					year.toString
				StdIn.readNonEmptyLine(s"Where do you want to save summary data? (default = $defaultPath)")
					.map { s => s: Path }.getOrElse(defaultPath)
		}
		connectionPool { implicit connection =>
			implicit val languageIds: LanguageIds = languageIdsPointer.value
			println(s"Exporting summary to ${path.toAbsolutePath}...")
			ExportSummary.asCsv(senderCompanyDetails.companyId, path, year, months) match {
				case Success(_) =>
					println("Summaries written")
					path.openInDesktop()
				case Failure(error) =>
					error.printStackTrace()
					println(s"Summary writing failed: ${error.getMessage}")
			}
		}
	}
	private def createInvoiceCommand(userId: Int, senderCompany: DetailedCompany) =
		Command.withoutArguments("invoice", "send", "Creates a new invoice") {
			connectionPool { implicit connection =>
				implicit val languageIds: LanguageIds = languageIdsPointer.value
				InvoiceActions.create(userId, senderCompany)
			}
		}
	
	// Updates the available commands when the user logs in / selects a company
	private val commandsPointer = userSettingsPointer.lazyMergeWith(companyPointer) { (user, company) =>
		val userStatefulCommands = user match {
			case Some(user) =>
				Vector(selectCompanyCommand(user.userId), claimCompanyCommand(user.userId),
					editCommand(user.userId, company.map { _.id })) ++
					company.toVector.flatMap { company => Vector(
						createInvoiceCommand(user.userId, company),
						printInvoiceCommand(user.userId, company.id)
					)}
			case None => Vector(loginCommand)
		}
		val companyStatefulCommands = company match {
			case Some(company) =>
				Vector(listCommand(company.id), cancelInvoiceCommand(company.id), exportDataCommand(company.details))
			case None => Vector()
		}
		(Vector(registerCommand, seeCommand(company.map { _.id }), backupCommand, importCommand,
			debugCommand, clearAllCommand) ++ GoldCommands.all ++ userStatefulCommands ++
			companyStatefulCommands)
			.sortBy { _.name }
	}
	
	// Starts the console
	println()
	println("Welcome to Arbiter")
	println("Instructions: use 'help' or 'man' commands to get more information. Use exit to quit.")
	Console(commandsPointer, "\nPlease enter the next command", closeConsoleFlag, closeCommandName = "exit").run()
	println("Bye!")
	println()
	
	System.exit(0)
}
