package vf.arbiter.command.app

import utopia.citadel.database.access.single.description.DbLanguageDescription
import utopia.citadel.database.access.single.language.DbLanguage
import utopia.citadel.model.enumeration.CitadelDescriptionRole.Name
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.time.TimeExtensions._
import utopia.flow.time.{Days, Now}
import utopia.flow.util.StringExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.view.immutable.caching.Lazy
import utopia.flow.view.mutable.Pointer
import utopia.flow.view.mutable.eventful.EventfulPointer
import utopia.metropolis.model.cached.LanguageIds
import utopia.metropolis.model.partial.description.DescriptionData
import utopia.vault.database.Connection
import vf.arbiter.command.database.access.many.device.DbInvoiceForms
import vf.arbiter.command.database.model.device.InvoiceFormModel
import vf.arbiter.command.model.cached.SelectedLanguage
import vf.arbiter.command.model.partial.device.InvoiceFormData
import vf.arbiter.core.controller.pdf.FillPdfForm
import vf.arbiter.core.database.access.many.company.DbCompanies
import vf.arbiter.core.database.access.many.invoice.{DbInvoiceItems, DbInvoices, DbItemUnits}
import vf.arbiter.core.database.access.single.company.{DbCompany, DbCompanyDetails}
import vf.arbiter.core.database.access.single.description.DbCompanyProductDescription
import vf.arbiter.core.database.access.single.invoice.{DbInvoice, DbItemUnit}
import vf.arbiter.core.database.model.CoreDescriptionLinkModel
import vf.arbiter.core.database.model.company.CompanyProductModel
import vf.arbiter.core.database.model.invoice.{InvoiceItemModel, InvoiceModel}
import vf.arbiter.core.model.combined.company._
import vf.arbiter.core.model.combined.invoice.{DescribedItemUnit, FullInvoice, FullInvoiceItem, InvoiceWithItems}
import vf.arbiter.core.model.enumeration.ArbiterDescriptionRoleId.Abbreviation
import vf.arbiter.core.model.partial.company.CompanyProductData
import vf.arbiter.core.model.partial.invoice.{InvoiceData, InvoiceItemData}
import vf.arbiter.core.model.stored.invoice.Invoice
import vf.arbiter.core.util.ReferenceCode

import java.nio.file.{Path, Paths}
import java.time.format.DateTimeFormatter
import scala.concurrent.duration.Duration
import scala.io.StdIn
import scala.util.{Failure, Random, Success}

/**
 * Contains interactive invoice-related actions
 * @author Mikko Hilpinen
 * @since 11.10.2021, v0.1
 */
object InvoiceActions
{
	// OTHER    -------------------------------
	
	/**
	 * Lists latest invoices
	 * @param senderCompanyId Id of the invoice sender company
	 * @param companyFilter A filter applied to recipient company name (optional)
	 * @param maxPast Maximum duration of invoices included (default = 30 days)
	 * @param connection Implicit DB Connection
	 * @param languageIds Implicit language ids
	 */
	def listLatest(senderCompanyId: Int, companyFilter: String = "", maxPast: Duration = 30.days)
	              (implicit connection: Connection, languageIds: LanguageIds) =
	{
		val baseAccess = maxPast.finite match {
			case Some(duration) => DbInvoices.createdAfter(Now - duration)
			case None => DbInvoices
		}
		// Finds the target company, if specified
		val recipientCompany = if (companyFilter.isEmpty) None else CompanyActions.findAndSelectOne(companyFilter)
		// Then finds the invoices
		val invoices = recipientCompany match {
			case Some(recipientCompany) => baseAccess.betweenCompanies(senderCompanyId, recipientCompany.id)
			case None => baseAccess.sentByCompanyWithId(senderCompanyId)
		}
		// Completes invoices and lists invoice information
		val fullInvoices = DbInvoices.complete(invoices).toVector.sortBy { _.created }
		println(f"Found ${fullInvoices.size} invoices. Total of ${
			fullInvoices.foldLeft(0.0) { _ + _.price }}%1.2f € (without VAT)")
		fullInvoices.reverseIterator.foreach { i =>
			println(s"\t- ${i.date}: ${i.id} ${i.recipientCompany.details.name} ${i.price} € (${i.referenceCode})")
		}
	}
	
	/**
	 * Creates a new invoice by interacting with the user
	 * @param userId Id of the user who creates this invoice
	 * @param senderCompany The company who's sending this invoice
	 * @param connection Implicit DB Connection
	 * @return Created invoice and its items. None if no invoice was created (process was cancelled by the user)
	 */
	def create(userId: Int, senderCompany: DetailedCompany)(implicit connection: Connection, languageIds: LanguageIds) =
	{
		// Selects the target company
		println("Which company this invoice is for?")
		println("Hint: If searching for an existing company, write a part of that company's name")
		StdIn.readNonEmptyLine().flatMap { CompanyActions.findOrCreateOne(userId, _) }.flatMap { targetCompany =>
			// Asks for basic information
			println("What language this invoice is in?")
			UserActions.selectOrAddKnownLanguage(userId).flatMap { language =>
				createOrEdit(userId, senderCompany, targetCompany, language, languageIds)
			}
		}
	}
	
	/**
	 * Finds an invoice and describes it on the console
	 * @param senderCompanyId Id of the company that sent the invoice
	 * @param filter Search filter, which may be company name part, invoice index or reference number (optional)
	 * @param connection Implicit DB Connection
	 */
	def findAndDescribe(senderCompanyId: Int, filter: String = "")(implicit connection: Connection) =
		findFullAnd(senderCompanyId, filter) { invoice =>
			// Prints information about the invoice
			println(s"\n----- INVOICE -----")
			println(s"Id: ${ invoice.id }")
			println(s"Reference Number: ${ invoice.referenceCode }")
			println(s"Date: ${ invoice.date }")
			println(s"Payment Duration: ${ invoice.paymentDuration }")
			println(s"Payment Deadline: ${ invoice.paymentDeadline }")
			println(s"Services Delivered: ${ invoice.productDeliveryDates }")
			println("----- SENDER -----")
			CompanyActions.describe(invoice.senderCompany)
			println("----- RECIPIENT -----")
			CompanyActions.describe(invoice.recipientCompany)
			println("----- ITEMS ------")
			invoice.items.foreachWithIndex { (item, index) =>
				println(s"- ${ index + 1 }: ${ item.description } (${ item.product.name }): ${item.unitsSold} ${
					item.unit.abbreviation} * ${item.pricePerUnit} €/${ item.unit.abbreviation } = ${item.price} € + ${
					(item.product.taxModifier * 100).toInt}% VAT (${ item.tax } €) = ${item.totalPrice} €")
			}
			println(s"-----\nTotal of ${ invoice.price } € + ${ invoice.tax } € VAT = ${ invoice.totalPrice } €")
			println("----- BANK -----")
			println(invoice.senderBankAccount)
			println("-----")
		}
	
	/**
	 * Finds an invoice and cancels it
	 * @param senderCompanyId Id of the company who sent the invoice
	 * @param filter Search filter, which may be company name part, invoice index or reference number (optional)
	 * @param connection Implicit DB Connection
	 */
	def findAndCancel(senderCompanyId: Int, filter: String = "")(implicit connection: Connection) =
		findAnd(senderCompanyId, filter) { (invoice, _) =>
			println("Invoice information:")
			println(s"Invoice number: ${invoice.id}")
			println(s"Date: ${invoice.date}")
			DbCompanyDetails(invoice.recipientCompanyDetailsId).name.foreach { recipientName =>
				println(s"Recipient: $recipientName")
			}
			println(s"Reference Code: ${invoice.referenceCode}")
			
			if (StdIn.ask("\nAre you sure you want to cancel this invoice?")) {
				invoice.access.cancel()
				println("The invoice cancelled!")
			}
			else
				println("The invoice was not cancelled.")
		}
	
	/**
	 * Finds an invoice and edits it
	 * @param userId Id of the user who's performing the edits
	 * @param senderCompanyId Id of the company who's sending the invoice
	 * @param filter Search filter, which may be company name part, invoice index or reference number (optional)
	 * @param connection Implicit DB connection
	 * @param languageIds Implicit list of user's preferred languages
	 */
	def findAndEdit(userId: Int, senderCompanyId: Int, filter: String = "")
	               (implicit connection: Connection, languageIds: LanguageIds) =
		findFullAnd(senderCompanyId, filter) { invoice =>
			// Reads required data
			// TODO: Handle thrown error
			// TODO: Add support for new language selection
			val languageName = DbLanguage(invoice.languageId).description.inLanguageWithId(invoice.languageId)
				.name.text.getOrElse("")
			println(s"Editing invoice ${invoice.id} for company ${invoice.recipientCompany.details.name}")
			createOrEdit(userId, invoice.senderCompany.toDetailedCompany,
				invoice.recipientCompany.toDetailedCompany, SelectedLanguage(invoice.languageId, languageName),
				languageIds, Some(invoice.toInvoiceWithItems))
		}
	
	/**
	 * Finds an invoice and prints it
	 * @param senderCompanyId Id of the company who sent the invoice
	 * @param filter Search filter, which may be company name part, invoice index or reference number (optional)
	 * @param connection Implicit DB Connection
	 */
	def findAndPrint(userId: Int, senderCompanyId: Int, filter: String = "")(implicit connection: Connection) =
		findFullAnd(senderCompanyId, filter) { invoice => print(userId, invoice) }
	
	/**
	 * Prints an invoice
	 * @param invoice Invoice to print - linked descriptions should in in invoice language
	 * @param connection Implicit DB Connection
	 */
	def print(userId: Int, invoice: FullInvoice)(implicit connection: Connection): Unit =
	{
		val languageName = DbLanguageDescription(invoice.languageId).name.inLanguageWithId(invoice.languageId).text
			.orElse(DbLanguage(invoice.languageId).isoCode).getOrElse("invoice language")
		selectInvoiceForm(userId, invoice.senderCompany.id, SelectedLanguage(invoice.languageId, languageName))
			.foreach { formPath =>
				val defaultFileName = s"${invoice.date}-${invoice.recipientCompany.details.name
					.replaceAll(" ", "-")}-${invoice.id}.pdf"
				val rawOutputPath: Path = Paths.get(StdIn.readNonEmptyLine(
					s"Please specify a path for the generated file (default = invoices/$defaultFileName)")
					.getOrElse(s"invoices/$defaultFileName"))
				val outputPath = {
					if (rawOutputPath.isDirectory)
						rawOutputPath/defaultFileName
					else if (rawOutputPath.fileType != "pdf")
						rawOutputPath.withMappedFileName { _.untilLast(".") + ".pdf" }
					else
						rawOutputPath
				}
				outputPath.createParentDirectories()
				
				println("Filling the invoice form...")
				val printFields = PrintFields.from(invoice)
				FillPdfForm(formPath.path, printFields, outputPath) match
				{
					case Success(failures) =>
						println("Form filled!")
						if (failures.nonEmpty)
						{
							failures.foreach { case (fieldName, error) =>
								println(s"Failed to write field: $fieldName = ${printFields(fieldName)}")
								error.printStackTrace()
							}
							println(s"Warning: ${failures.size} fields were not written correctly")
						}
						outputPath.openInDesktop()
						// Allows the user to flatten the document afterwards
						if (StdIn.ask("Do you want to flatten this document so that it's no longer editable?")) {
							val flatPath = outputPath.withMappedFileName { name =>
								val (namePart, extension) = name.splitAtLast(".").toTuple
								s"$namePart-flat.$extension"
							}
							FillPdfForm.flatten(outputPath, flatPath) match {
								case Success(_) =>
									println("Document flattened")
									flatPath.openFileLocation()
								case Failure(error) =>
									error.printStackTrace()
									println("Failed to flatten the document. See error above.")
							}
						}
					case Failure(error) =>
						error.printStackTrace()
						println(s"Printing failed due to error: ${error.getMessage}")
				}
			}
	}
	
	private def findFullAnd[U](senderCompanyId: Int, filter: String = "")(action: FullInvoice => U)
	                          (implicit connection: Connection) = {
		// Finds the invoice
		findAnd(senderCompanyId, filter) { (invoice, senderDetails) =>
			// Loads recipient information
			invoice.recipientDetailsAccess.full.foreach { recipientDetails =>
				// Loads company information
				val companiesById = DbCompanies(
					Set(senderDetails.companyId, recipientDetails.companyId)).pull
					.map { c => c.id -> c }.toMap
				// Loads banking information
				invoice.bankAccountAccess.full.foreach { bank =>
					// Loads invoice items
					val items = invoice.access.items.fullInLanguageWithId(invoice.languageId)
					// Combines information together
					val sender = FullyDetailedCompany(companiesById(senderDetails.companyId), senderDetails)
					val recipient = FullyDetailedCompany(
						companiesById(recipientDetails.companyId), recipientDetails)
					// Performs the actual action
					action(FullInvoice(invoice, sender, recipient, bank, items))
				}
			}
		}
	}
	
	private def findAnd[U](senderCompanyId: Int, filter: String = "")
	                      (action: (Invoice, FullCompanyDetails) => U)
	                      (implicit connection: Connection) =
	{
		println("Which key you want to search with?")
		ActionUtils.selectFrom(Vector(1 -> "Invoice index", 2 -> "Reference number", 3 -> "Customer name"),
			"search keys", "use", skipQuestion = true).foreach { searchKey =>
			val searchKeyName = {
				if (searchKey == 1)
					"invoice index"
				else if (searchKey == 2)
					"reference number"
				else
					"customer name"
			}
			filter.notEmpty.orElse { StdIn.readNonEmptyLine(s"What's the $searchKeyName you want to find?") }
				.foreach { searched =>
					// Finds the invoice with the searched key
					val invoice = {
						import utopia.flow.generic.casting.ValueConversions._
						if (searchKey == 1)
							searched.int match {
								case Some(invoiceId) => DbInvoice(invoiceId).pull
								case None =>
									println(s"$searched is not a valid invoice index")
									None
							}
						else if (searchKey == 2)
							DbInvoice.withReferenceCode(searched)
						else
							CompanyActions.findAndSelectOne(searched).flatMap { recipientCompany =>
								val invoices = DbInvoices.betweenCompanies(senderCompanyId, recipientCompany.id)
								ActionUtils.selectFrom(invoices.map { i => i -> s"${i.date}: ${i.id} / ${i.referenceCode}" })
							}
					}
					invoice.foreach { invoice =>
						// Reads associated data
						// The invoice must belong to the sender company
						invoice.senderDetailsAccess.full.filter { _.companyId == senderCompanyId } match {
							case Some(senderDetails) => action(invoice, senderDetails)
							case None => println("This invoice doesn't belong to your company")
						}
					}
			}
		}
	}
	
	private def createOrEdit(userId: Int, senderCompany: DetailedCompany, recipientCompany: DetailedCompany,
	                         invoiceLanguage: SelectedLanguage, otherLanguageIds: LanguageIds,
	                         editedInvoice: Option[InvoiceWithItems] = None)
	                        (implicit connection: Connection) =
	{
		implicit val appliedLanguageIds: LanguageIds = otherLanguageIds.preferringLanguageWithId(invoiceLanguage.id)
		
		// Requests the payment duration & product delivery dates -information
		val (duration, deliveryDate) = editedInvoice.map { i => i.paymentDuration -> i.productDeliveryDates }
			// Editing: Asks if the user wants to change these
			.filter { case (duration, delivery) =>
				val datesStr = delivery match {
					case Some(d) => d.toString
					case None => "N/A"
				}
				StdIn.ask(s"Do you want to keep the previous payment duration (${
					duration.length} days) and delivery dates ($datesStr)?", default = true)
			}
			.getOrElse {
				val duration = Days(StdIn.read(
						"How many days does the company have time to pay this bill? (default = 30)")
					.intOr(30))
				println("When were the services or items delivered for the customer?")
				println("Leave empty if not applicable")
				val deliveryDate = ActionUtils.readDateRange()
				duration -> deliveryDate
			}
		
		// Prepares information for the next phase
		// Units must have some kind of description available
		val readUnits = DbItemUnits.described.filter { u => u.has(Name) || u.has(Abbreviation) }
			.sortBy { _.wrapped.categoryId }
		if (readUnits.isEmpty)
			None
		else {
			// Also makes sure units have appropriate names in the targeted language
			val units = readUnits.map { u =>
				val (unitPlaceholderName, hasName, hasAbbreviation) = u.description(Name) match {
					case Some(nameDescription) =>
						val hasAbbreviation = u.description(Abbreviation).exists { _.languageId == invoiceLanguage.id }
						if (nameDescription.languageId == invoiceLanguage.id)
							(nameDescription.text, true, hasAbbreviation)
						else
							(nameDescription.text, false, hasAbbreviation)
					case None =>
						val abbreviation = u.description(Abbreviation).get
						(abbreviation.text, false, abbreviation.languageId == invoiceLanguage.id)
				}
				// Case: Required data already exists
				if (hasName && hasAbbreviation)
					u
				// Case: Some data is missing => asks the user to fill it in
				else {
					val newName = if (hasName) None else StdIn.readNonEmptyLine(
						s"What's the name of $unitPlaceholderName in ${invoiceLanguage.name}")
					val newAbbreviation = if (hasAbbreviation) None else
						StdIn.readNonEmptyLine(s"What's the abbreviation of ${
							newName.getOrElse(unitPlaceholderName)
						} in ${invoiceLanguage.name}?")
					val newDescriptionData = (newName.map { _ -> Name.id } ++
						newAbbreviation.map { _ -> Abbreviation.id })
						.map { case (text, roleId) => DescriptionData(roleId, invoiceLanguage.id, text, Some(userId)) }
						.toVector
					val newDescriptions = CoreDescriptionLinkModel.itemUnit.insert(u.id, newDescriptionData)
					// Replaces the existing descriptions with the new ones
					newDescriptions.foldLeft(u) { (u, newDescription) =>
						u.copy(descriptions = u.descriptions
							.filter { _.description.roleId != newDescription.description.roleId } + newDescription)
					}
				}
			}
			// [Product -> filled product] - separated because filled product may require user interaction
			val existingProductsPointer = Pointer(DbCompany(senderCompany.id).products.described
				.map { p => p -> Lazy { fillProduct(userId, p, invoiceLanguage, units) } })
			
			// Creates / prepares the invoice items
			val lastProductPointer = EventfulPointer.empty[FullCompanyProduct]()
			lastProductPointer.addContinuousListener {
				_.newValue.flatMap { _(Name).notEmpty }
					.foreach { n => println(s"Using product $n for this invoice item") }
			}
			var lastProductPrice: Option[Double] = None
			
			// Edits, keeps or removes the existing invoice items (editing mode only)
			val keptItems = editedInvoice match {
				case Some(i) =>
					i.items.flatMap { item =>
						StdIn.printAndReadLine(s"Do you want to keep (K), edit (e) or remove (r) ${
							item.description}?").headOption.getOrElse('k').toLower match
						{
							case 'r' => None
							case 'e' =>
								requestInvoiceItem(userId, senderCompany.id, invoiceLanguage,
									existingProductsPointer.value.find { _._1.id == item.productId }.map { _._2.value },
									Some(item.pricePerUnit), existingProductsPointer, units, Some(item.data))
									.map { Left(_) }
							case _ => Some(Right(item))
						}
					}
				case None => Vector()
			}
			
			// Collected info: product id + description + amount + price per unit
			val newInvoiceItemData = Iterator.iterate(1) { _ + 1 }.map { index =>
				// Asks whether to stop iterating
				if ((index <= 1 && keptItems.isEmpty) ||
					StdIn.ask("Do you want to add another item to this invoice?"))
				{
					val itemData = requestInvoiceItem(userId, senderCompany.id, invoiceLanguage,
						lastProductPointer.value, lastProductPrice, existingProductsPointer, units)
					// Keeps track of the latest selected product & price in order to provide better defaults
					itemData.foreach { case (product, _, price, _) =>
						lastProductPointer.value = Some(product)
						lastProductPrice = Some(price)
					}
					itemData
				}
				else
					None
			}.takeWhile { _.isDefined }.toVector.flatten
			
			// Saves the invoice
			if (newInvoiceItemData.nonEmpty || keptItems.nonEmpty ||
				StdIn.ask("You didn't register any invoice items. Do you still want to save this invoice?"))
			{
				// Selects a bank account
				editedInvoice.flatMap { _.bankAccountAccess.full }
					.filter { account =>
						StdIn.ask(s"Do you want to use the same bank account (in ${account.bank.name})?",
							default = true)
					}
					.orElse { BankActions.selectOrCreateBankAccount(userId, senderCompany.id) }
					.map { bankAccount =>
						// Cancels the old invoice version, if applicable
						editedInvoice.foreach { i => DbInvoice(i.id).cancel() }
						// TODO: Should check whether reference code is a duplicate
						val invoice = InvoiceModel.insert(InvoiceData(senderCompany.details.id, recipientCompany.details.id,
							bankAccount.id, invoiceLanguage.id,
							ReferenceCode(userId, senderCompany.id, recipientCompany.id, Random.nextInt(1000)),
							duration, deliveryDate, Some(userId)))
						val fullInvoiceItemData = keptItems.map {
							case Right(item) => item.data.copy(invoiceId = invoice.id)
							case Left((product, description, pricePerUnit, amount)) =>
								InvoiceItemData(invoice.id, product.id, description, pricePerUnit, amount)
						} ++ newInvoiceItemData
							.map { case (product, description, pricePerUnit, amount) =>
								InvoiceItemData(invoice.id, product.id, description, pricePerUnit, amount)
							}
						val invoiceItems = InvoiceItemModel.insert(fullInvoiceItemData)
						
						// May print the invoice afterwards
						if (StdIn.ask("Do you want to export this invoice into a pdf?")) {
							// Creates full invoice data
							senderCompany.details.addressAccess.full.flatMap { senderAddress =>
								recipientCompany.details.addressAccess.full.map { recipientAddress =>
									val fullInvoiceItems = DbInvoiceItems(invoiceItems.map { _.id }.toSet).full
									FullInvoice(invoice, senderCompany + senderAddress,
										recipientCompany + recipientAddress, bankAccount, fullInvoiceItems)
								}
							} match {
								case Some(invoice) => print(userId, invoice)
								case None => println("Some required data was missing (inform the developer of this problem)")
							}
						}
						
						invoice.withItems(invoiceItems)
					}
			}
			else
				None
		}
	}
	
	// Returns product + description + units sold + price per unit
	private def requestInvoiceItem(userId: Int, senderCompanyId: Int, language: SelectedLanguage,
	                               defaultProduct: Option[FullCompanyProduct], defaultPrice: Option[Double],
	                               existingProductsPointer: Pointer[Vector[(DescribedCompanyProduct, Lazy[FullCompanyProduct])]],
	                               units: Vector[DescribedItemUnit], editedItem: Option[InvoiceItemData] = None)
	                              (implicit connection: Connection) =
	{
		// Selects the product to use
		val product: Option[FullCompanyProduct] = defaultProduct
			// Option A: Use same product as for the last line
			.filter { product =>
				StdIn.ask(s"Is the next item of the same product (${product.name})?")
			}
			// Option B: Selects from existing products or creates a new product
			.orElse {
				println("Please select or insert the product to use in this invoice item")
				ActionUtils.selectOrInsert(
					existingProductsPointer.value.map { case (p, fullP) => fullP -> p.name }, "product") {
					val newProduct = createProduct(userId, senderCompanyId, language, units)
					// Adds the product to selectable options
					newProduct.foreach { p => existingProductsPointer.update { _ :+ (p.describedProduct, Lazy(p)) } }
					newProduct.map(Lazy.initialized)
				}.map { _.value }
			}
		
		product.map { product =>
			// Collects invoice item information
			val productName = product.name
			val unitName = product.unit.name
			val (defaultDescription, defaultUnitsSold) = editedItem match {
				case Some(i) => (i.description, i.unitsSold)
				case None => (productName, 1.0)
			}
			println("Please add a short description for this invoice item")
			println(s"Leaving this empty will yield: $defaultDescription")
			val description = StdIn.readNonEmptyLine().getOrElse(defaultDescription)
			println(s"How many units ($unitName) of $productName were sold? (default = $defaultUnitsSold)")
			println("Hint: Allows for decimal numbers")
			val amount = StdIn.read().doubleOr(defaultUnitsSold)
			println(s"What's the price of a single $unitName of $productName (without applying any taxes)?")
			val pricePerUnit = defaultPrice.orElse(product.product.defaultUnitPrice) match {
				case Some(default) =>
					println(s"Default = $default €/$unitName")
					StdIn.read().doubleOr(default)
				case None => StdIn.readIterator.flatMap { _.double }.next()
			}
			// Collects the information together
			(product, description, pricePerUnit, amount)
		}
	}
	
	private def fillProduct(userId: Int, product: DescribedCompanyProduct, language: SelectedLanguage,
	                        units: Iterable[DescribedItemUnit])
	                       (implicit connection: Connection, languageIds: LanguageIds) =
	{
		// NB: Unit read may technically fail
		val productUnit = units.find { _.id == product.wrapped.unitId }
			.getOrElse { DbItemUnit(product.wrapped.unitId).described.get }
		// Case: Product already has a name in the correct language => uses as is
		if (product.description(Name).exists { _.languageId == language.id })
			FullCompanyProduct(product, productUnit)
		// Case: Product doesn't have a name in the correct language => asks for one
		else
			StdIn.readNonEmptyLine(s"What's the name of ${
				product(Name).nonEmptyOrElse("this product")} in ${language.name}?") match
			{
				case Some(newName) =>
					val newDescription = CoreDescriptionLinkModel.companyProduct
						.insert(product.id, DescriptionData(Name.id, language.id,
							newName, Some(userId)))
					val modifiedProduct = product.copy(descriptions =
						product.descriptions.filter { _.description.roleId != Name.id } +
							newDescription)
					FullCompanyProduct(modifiedProduct, productUnit)
				case None => FullCompanyProduct(product, productUnit)
			}
	}
	
	private def createProduct(userId: Int, senderCompanyId: Int, language: SelectedLanguage,
	                          units: Seq[DescribedItemUnit])(implicit connection: Connection) =
	{
		val retryPrompt = "This information is required. Leaving empty will cancel invoice creation."
		StdIn.readNonEmptyLine(s"What's the name of this new product in ${language.name}?", retryPrompt).flatMap { name =>
			println("What's the unit in which this product is sold (select from below)")
			ActionUtils.selectFrom(units.map { u => u -> u.apply(Name, Abbreviation).nonEmptyOrElse("?") },
				skipQuestion = true).map { selectedUnit =>
				val defaultPrice = StdIn.read(
					s"What's the default price (€) of this product for one ${
						selectedUnit.name }? (optional)").double
				val taxModifier = StdIn.read(
					"What's the tax percentage applied for this product? (default = 24%)")
					.double.map { _ / 100.0 }.getOrElse(0.24)
				
				// Inserts the product and it's name to the database
				val product = CompanyProductModel.insert(CompanyProductData(senderCompanyId,
					selectedUnit.id, defaultPrice, taxModifier, Some(userId)))
				val nameDescription = DbCompanyProductDescription.linkModel
					.insert(product.id, DescriptionData(Name.id, language.id, name, Some(userId)))
				val describedProduct = DescribedCompanyProduct(product, Set(nameDescription))
				
				FullCompanyProduct(describedProduct, selectedUnit)
			}
		}
	}
	
	private def selectInvoiceForm(userId: Int, companyId: Int, language: SelectedLanguage)
	                             (implicit connection: Connection) =
	{
		def _ask(path: Path) = StdIn.ask(s"Use invoice form $path", default = true)
		
		// Finds the existing form records
		val storedForms = DbInvoiceForms.forUserWithId(userId).withLanguageId(language.id).pull
		// Checks which of the forms still exists and removes the others
		val (notExistingForms, existingForms) = storedForms.divideBy { _.path.exists }.toTuple
		if (notExistingForms.nonEmpty)
			DbInvoiceForms(notExistingForms.map { _.id }).delete()
		
		val (notMyCompanyForms, myCompanyForms) = existingForms.divideBy { _.companyId.contains(companyId) }.toTuple
		// May offer an existing path if there is only one option available
		if (myCompanyForms.size == 1 && _ask(myCompanyForms.head.path))
			Some(myCompanyForms.head)
		else {
			// Allows the user to select or create a new form path
			val (generalForms, otherCompanyForms) = notMyCompanyForms.divideBy { _.companyId.isDefined }.toTuple
			val orderedForms = myCompanyForms.sortBy { _.path.toString } ++ generalForms.sortBy { _.path.toString } ++
				otherCompanyForms.sortBy { _.path.toString }
			val newForm = ActionUtils.selectOrInsert(orderedForms.map { f => f -> f.path.toString }, "form",
				skipInsertQuestion = true) {
				println(s"Please type the path to the invoice form to use (in ${language.name})")
				val root: Path = ""
				println(s"Hint: The path may be absolute or relative to ${root.toAbsolutePath}")
				val somePaths = root.allChildrenIterator.flatMap { _.toOption }.filter { _.fileType == "pdf" }.take(5)
				if (somePaths.nonEmpty) {
					println("Some paths that were found:")
					somePaths.foreach { p => println("- " + root.relativize(p)) }
				}
				StdIn.readValidOrEmpty() { v =>
					val path: Path = v.getString
					if (path.exists) {
						if (path.fileType == "pdf")
							Right(path)
						else
							Left("Specified path is not a pdf form. Please try again or leave empty to cancel.")
					}
					else
						Left("There doesn't exist any file at the specified path. Please try again or leave empty to cancel.")
				}.map { path => InvoiceFormModel.insert(InvoiceFormData(userId, language.id, Some(companyId), path)) }
			}
			// Generalizes the form if necessary
			newForm.filter { _.companyId.exists { _ != companyId } }.foreach { _.access.generalize() }
			newForm
		}
	}
	
	
	// NESTED   ------------------------------
	
	private object PrintFields
	{
		private val dateFormat = DateTimeFormatter.ofPattern("dd.MM.uuuu")
		
		private val invoiceNumber = "invoice-index"
		private val referenceCode = "reference-code"
		
		private val date = "date"
		private val duration = "pay-duration"
		private val deadline = "payment-deadline"
		private val delivery = "delivery-date"
		
		private val totalPrice = "payment-total"
		private val totalTax = "tax-total"
		private val totalPriceTaxed = "payment-total-taxed"
		
		object Sender
		{
			private val name = "sender-name"
			private val yCode = "sender-y-code"
			private val taxCode = "sender-tax-code"
			private val address = "sender-address"
			private val postalCode = "sender-postal-code"
			private val iban = "sender-bank"
			private val bic = "sender-bic"
			
			def from(senderCompany: FullyDetailedCompany, bankAccount: FullCompanyBankAccount) =
			{
				val senderName = senderCompany.details.name
				
				Map[String, String](
					name -> senderName,
					s"$name-2" -> senderName,
					yCode -> senderCompany.yCode,
					taxCode -> senderCompany.details.taxCode.getOrElse(""),
					address -> senderCompany.details.address.address.toString,
					postalCode -> senderCompany.details.address.postalCode.toString,
					iban -> bankAccount.address,
					bic -> bankAccount.bank.bic
				)
			}
		}
		
		object Recipient
		{
			private val name = "buyer-name"
			private val yCode = "buyer-y-code"
			private val id = "customer-code"
			private val address = "buyer-address"
			private val postalCode = "buyer-postal-code"
			
			def from(recipientCompany: FullyDetailedCompany) =
			{
				val recipientName = recipientCompany.details.name
				val recipientAddress = recipientCompany.details.address.address.toString
				val recipientPostal = recipientCompany.details.address.postalCode.toString
				
				Map[String, String](
					name -> recipientName,
					s"$name-2" -> recipientName,
					yCode -> recipientCompany.yCode,
					id -> recipientCompany.id.toString,
					address -> recipientAddress,
					s"$address-2" -> recipientAddress,
					postalCode -> recipientPostal,
					s"$postalCode-2" -> recipientPostal)
			}
		}
		
		object ItemRow
		{
			private val name = "name"
			private val amount = "amount"
			private val unit = "unit"
			private val unitPrice = "unit-price"
			private val price = "price"
			private val taxPercent = "tax"
			private val totalPrice = "price-taxed"
			
			def from(item: FullInvoiceItem, index: Int) = {
				val unitName = item.product.unit.abbreviation
				val taxMod = item.product.product.taxModifier
				
				val prefix = s"p${index + 1}-"
				Map[String, String](
					(prefix + name) -> item.description,
					(prefix + amount) -> round(item.unitsSold),
					(prefix + unit) -> unitName,
					(prefix + unitPrice) -> (round(item.pricePerUnit) + s" €/$unitName"),
					(prefix + price) -> (round(item.price) + " €"),
					(prefix + taxPercent) -> s"${(taxMod * 100).round}%",
					(prefix + totalPrice) -> (round(item.price * (1 + taxMod)) + " €")
				)
			}
		}
		
		def from(invoice: FullInvoice) =
		{
			val price = invoice.price
			val tax = invoice.tax
			val priceWithTax = round(price + tax) + " €"
			val dl = dateFormat.format(invoice.paymentDeadline)
			
			Map[String, String](
				invoiceNumber -> invoice.id.toString,
				referenceCode -> invoice.referenceCode,
				s"$referenceCode-2" -> invoice.referenceCode,
				date -> dateFormat.format(invoice.date),
				duration -> s"${invoice.paymentDuration.length} pv netto",
				deadline -> dateFormat.format(invoice.paymentDeadline),
				s"$deadline-2" -> dl,
				delivery -> invoice.productDeliveryDates.map { _.toString }.getOrElse(""),
				totalPrice -> (round(price) + " €"),
				totalTax -> (round(tax) + " €"),
				totalPriceTaxed -> priceWithTax,
				s"$totalPriceTaxed-2" -> priceWithTax
			) ++ Sender.from(invoice.senderCompany, invoice.senderBankAccount) ++
				Recipient.from(invoice.recipientCompany) ++
				invoice.items.zipWithIndex.flatMap { case (item, index) => ItemRow.from(item, index) }
		}
		
		private def round(price: Double) = f"${(price * 100.0).round / 100.0}%1.2f"
	}
}
