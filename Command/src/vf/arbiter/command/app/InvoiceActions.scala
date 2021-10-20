package vf.arbiter.command.app

import utopia.citadel.database.access.single.description.DbLanguageDescription
import utopia.citadel.model.enumeration.CitadelDescriptionRole.Name
import utopia.flow.datastructure.mutable.PointerWithEvents
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Days
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.FileExtensions._
import utopia.flow.util.StringExtensions._
import utopia.metropolis.model.cached.LanguageIds
import utopia.metropolis.model.partial.description.DescriptionData
import utopia.vault.database.Connection
import vf.arbiter.command.model.SelectedLanguage
import vf.arbiter.core.controller.pdf.FillPdfForm
import vf.arbiter.core.database.access.many.company.{DbBanks, DbCompanies}
import vf.arbiter.core.database.access.many.invoice.{DbInvoices, DbItemUnits}
import vf.arbiter.core.database.access.single.company.DbCompany
import vf.arbiter.core.database.access.single.description.DbCompanyProductDescription
import vf.arbiter.core.database.access.single.invoice.{DbInvoice, DbItemUnit}
import vf.arbiter.core.database.model.CoreDescriptionLinkModel
import vf.arbiter.core.database.model.company.{BankModel, CompanyBankAccountModel, CompanyProductModel}
import vf.arbiter.core.database.model.invoice.{InvoiceItemModel, InvoiceModel}
import vf.arbiter.core.model.combined.company.{DescribedCompanyProduct, DetailedCompany, FullCompanyBankAccount, FullCompanyProduct, FullyDetailedCompany}
import vf.arbiter.core.model.combined.invoice.{FullInvoice, FullInvoiceItem}
import vf.arbiter.core.model.enumeration.ArbiterDescriptionRoleId.Abbreviation
import vf.arbiter.core.model.partial.company.{BankData, CompanyBankAccountData, CompanyProductData}
import vf.arbiter.core.model.partial.invoice.{InvoiceData, InvoiceItemData}
import vf.arbiter.core.util.ReferenceCode

import java.nio.file.{Path, Paths}
import java.time.format.DateTimeFormatter
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
	 * Asks the user to select a bank account. Also allows account creation.
	 * @param userId Id of the user who's selecting the account
	 * @param companyId Id of the company for which the account is selected
	 * @param connection Implicit DB connection
	 * @return Selected or created account. None if no account was selected or created.
	 */
	def selectOrCreateBankAccount(userId: Int, companyId: Int)(implicit connection: Connection) =
	{
		val existingAccounts = DbCompany(companyId).fullBankAccounts
		if (existingAccounts.isEmpty)
			createBankAccount(userId, companyId)
		else
			ActionUtils.selectFrom(existingAccounts.map { a => a -> s"${a.bank.name}: ${a.address}" }).orElse {
				if (StdIn.ask("Would you add a new bank account instead?"))
					createBankAccount(userId, companyId)
				else
					None
			}
	}
	
	/**
	 * Creates a new bank account for the specified company
	 * @param userId Id of the user adding the bank account
	 * @param companyId Id of the company for which the bank account is added
	 * @param connection Implicit DB Connection
	 * @return Inserted bank account. None if user cancelled.
	 */
	def createBankAccount(userId: Int, companyId: Int)(implicit connection: Connection) =
		selectOrCreateBank(userId).flatMap { bank =>
			println(s"What's your bank account number (IBAN) in ${bank.name}?")
			StdIn.readNonEmptyLine().map { address =>
				// TODO: Should check for duplicates
				val newAccount = CompanyBankAccountModel.insert(
					CompanyBankAccountData(companyId, bank.id, address, Some(userId)))
				FullCompanyBankAccount(newAccount, bank)
			}
		}
	
	/**
	 * Allows the user to select from existing banks or to create a new one
	 * @param userId Id of the user
	 * @param connection Implicit DB Connection
	 * @return Selected or created bank. None if user didn't want to select nor create.
	 */
	def selectOrCreateBank(userId: Int)(implicit connection: Connection) =
	{
		val existingBanks = DbBanks.pull
		if (existingBanks.isEmpty)
			createBank(userId)
		else
			ActionUtils.selectFrom(existingBanks.map { b => b -> b.nameAndBic }, "banks").orElse {
				if (StdIn.ask("Would you like to add a new bank instead?"))
					createBank(userId)
				else
					None
			}
	}
	
	/**
	 * Creates a new bank, provided the user gives the necessary information
	 * @param userId Id of the user who's adding information
	 * @param connection Implicit DB Connection
	 * @return Created bank. None if user didn't provide information.
	 */
	// TODO: Should probably check for duplicates
	def createBank(userId: Int)(implicit connection: Connection) =
		StdIn.readNonEmptyLine("What's the name of the bank you're using?").flatMap { name =>
			StdIn.readNonEmptyLine("What's the BIC of the bank you're using?").map { bic =>
				BankModel.insert(BankData(name, bic, Some(userId)))
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
			val language = ActionUtils.forceSelectKnownLanguage()
			_create(userId, senderCompany, targetCompany, language, languageIds)
		}
	}
	
	/**
	 * Finds an invoice and prints it
	 * @param senderCompanyId Id of the company who sent the invoice
	 * @param connection Implicit DB Connection
	 */
	def findAndPrint(senderCompanyId: Int)(implicit connection: Connection) =
	{
		println("Which key you want to search with?")
		ActionUtils.selectFrom(Vector(1 -> "Invoice index", 2 -> "Reference number", 3 -> "Customer name"),
			"search keys", "use", skipQuestion = true).foreach { searchKey =>
			val searchKeyName = if (searchKey == 1) "invoice index" else if (searchKey == 2) "reference number" else
				"customer name"
			StdIn.readNonEmptyLine(s"What's the $searchKeyName you want to find?").foreach { searched =>
				// Finds the invoice with the searched key
				val invoice = {
					if (searchKey == 1)
						searched.int match
						{
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
					invoice.senderDetailsAccess.full.filter { _.companyId == senderCompanyId } match
					{
						case Some(senderDetails) =>
							invoice.recipientDetailsAccess.full.foreach { recipientDetails =>
								val companiesById = DbCompanies(
									Set(senderDetails.companyId, recipientDetails.companyId)).pull
									.map { c => c.id -> c }.toMap
								invoice.bankAccountAccess.full.foreach { bank =>
									val items = invoice.access.items.fullInLanguageWithId(invoice.languageId)
									val sender = FullyDetailedCompany(companiesById(senderDetails.companyId),
										senderDetails)
									val recipient = FullyDetailedCompany(
										companiesById(recipientDetails.companyId), recipientDetails)
									print(FullInvoice(invoice, sender, recipient, bank, items))
								}
							}
						case None => println("This invoice doesn't belong to your company")
					}
				}
			}
		}
	}
	
	/**
	 * Prints an invoice
	 * @param invoice Invoice to print - linked descriptions should in in invoice language
	 * @param connection Implicit DB Connection
	 */
	def print(invoice: FullInvoice)(implicit connection: Connection): Unit =
	{
		println("Please type the path to the invoice form pdf file")
		val root = Paths.get("")
		println(s"Instructions: You can specify an absolute path or a path relative to ${root.toAbsolutePath}")
		DbLanguageDescription(invoice.languageId).name.inLanguageWithId(invoice.languageId).foreach { langDesc =>
			val langName = langDesc.description.text
			println(s"The invoice is in $langName, so you may want to pick a $langName template")
		}
		val somePaths = root.allChildrenIterator.flatMap { _.toOption }.filter { _.fileType == "pdf" }.take(5)
		if (somePaths.nonEmpty)
		{
			println("Some paths that were found:")
			somePaths.foreach { p => println("- " + root.relativize(p)) }
		}
		StdIn.readNonEmptyLine() match
		{
			case Some(pathString) =>
				val path: Path = pathString
				if (path.isRegularFile && path.fileType == "pdf")
				{
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
					FillPdfForm(path, printFields, outputPath) match
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
						case Failure(error) =>
							error.printStackTrace()
							println(s"Printing failed due to error: ${error.getMessage}")
					}
				}
				else
					println(s"${path.toAbsolutePath} is not an existing pdf")
			case None => println("Printing cancelled")
		}
	}
	
	private def _create(userId: Int, senderCompany: DetailedCompany, recipientCompany: DetailedCompany,
	                    invoiceLanguage: SelectedLanguage, otherLanguageIds: LanguageIds)
	                   (implicit connection: Connection) =
	{
		implicit val appliedLanguageIds: LanguageIds = otherLanguageIds.preferringLanguageWithId(invoiceLanguage.id)
		
		val duration = Days(StdIn.read(
			"How many days does the company have time to pay this bill? (default = 30)")
			.intOr(30))
		println("When were the services or items delivered for the customer?")
		println("Leave empty if not applicable")
		val deliveryDate = ActionUtils.readDate()
		
		// Prepares information for the next phase
		// Units must have some kind of description available
		val readUnits = DbItemUnits.described.filter { u => u.has(Name) || u.has(Abbreviation) }
			.sortBy { _.wrapped.categoryId }
		if (readUnits.isEmpty)
			None
		else
		{
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
				else
				{
					val newName = if (hasName) None else StdIn.readNonEmptyLine(
						s"What's the name of $unitPlaceholderName in ${invoiceLanguage.name}")
					val newAbbreviation = if (hasAbbreviation) None else
						StdIn.readNonEmptyLine(s"What's the abbreviation of ${
							newName.getOrElse(unitPlaceholderName)} in ${invoiceLanguage.name}?")
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
			var existingProducts = DbCompany(senderCompany.id).products.described
			
			// Creates / prepares the invoice items
			val lastProductPointer = new PointerWithEvents[Option[FullCompanyProduct]](None)
			lastProductPointer.addListener { _.newValue.flatMap { _(Name) }
				.foreach { n => println(s"Using product $n for the next invoice item") } }
			// Collected info: product id + description + amount + price per unit
			val invoiceItemData = Iterator.iterate(1) { _ + 1 }.map { index =>
				// Asks whether to stop iterating
				if (index <= 1 || StdIn.ask("Do you want to add another item to this invoice?"))
				{
					// Selects the product to use
					val product: Option[FullCompanyProduct] = lastProductPointer.value
						// Option A: Use same product as for the last line
						.filter { product =>
							StdIn.ask(s"Is the next item of the same product (${
								product(Name).getOrElse(s"Unnamed product #${product.id}") })?")
						}
						// Option B: Select from existing products
						.orElse {
							if (existingProducts.nonEmpty)
								ActionUtils.selectFrom(existingProducts.map { p =>
									p -> p(Name).getOrElse(s"Unnamed product #${p.id}") },
									"products", "refer to")
									// Makes sure the product has a name in the correct language
									.map { p =>
										// Includes unit information also
										// TODO: Read might technically fail (see get below)
										// TODO: The selectable options should be FullCompanyProducts
										val productUnit = units.find { _.id == p.wrapped.unitId }
											.getOrElse { DbItemUnit(p.wrapped.unitId).described.get }
										if (p.description(Name).exists { _.languageId == invoiceLanguage.id })
											FullCompanyProduct(p, productUnit)
										else
											StdIn.readNonEmptyLine(s"What's the name of ${
												p(Name).getOrElse("this product")} in ${invoiceLanguage.name}?") match
											{
												case Some(newName) =>
													val newDescription = CoreDescriptionLinkModel.companyProduct
														.insert(p.id, DescriptionData(Name.id, invoiceLanguage.id,
															newName, Some(userId)))
													val modifiedProduct = p.copy(descriptions =
														p.descriptions.filter { _.description.roleId != Name.id } +
															newDescription)
													existingProducts = existingProducts
														.filterNot { _.id == p.id } :+ modifiedProduct
													FullCompanyProduct(modifiedProduct, productUnit)
												case None => FullCompanyProduct(p, productUnit)
											}
									}
							else
								None
						}
						// Option C: Create a new product
						.orElse {
							if (StdIn.ask("Is it okay to create a new product? (no quits adding invoice items)",
								default = true))
								StdIn.readNonEmptyLine(s"What's the name of this new product in ${
									invoiceLanguage.name}?").map { name =>
									println("What's the unit in which this product is sold (select from below)")
									val selectedUnit = ActionUtils.forceSelectFrom(
										units.map { u => u -> u(Name).getOrElse(u(Abbreviation).getOrElse("?")) })
									val defaultPrice = StdIn.read(
										s"What's the default price (€) of this product for one ${
											selectedUnit(Name).getOrElse(selectedUnit(Abbreviation).getOrElse("unit"))
										}? (optional)").double
									val taxModifier = StdIn.read(
										"What's the tax percentage applied for this product? (default = 24%)")
										.double.map { _ / 100.0 }.getOrElse(0.24)
									
									// Inserts the product and it's name to the database
									val product = CompanyProductModel.insert(CompanyProductData(senderCompany.id,
										selectedUnit.id, defaultPrice, taxModifier, Some(userId)))
									val nameDescription = DbCompanyProductDescription.linkModel
										.insert(product.id, DescriptionData(Name.id, invoiceLanguage.id, name,
											Some(userId)))
									val describedProduct = DescribedCompanyProduct(product, Set(nameDescription))
									
									// Adds this product to existing options
									existingProducts :+= describedProduct
									FullCompanyProduct(describedProduct, selectedUnit)
								}
							else
								None
						}
					
					lastProductPointer.value = product
					product.map { product =>
						// Collects invoice item information
						val productName = product(Name).getOrElse(s"Unnamed product #${product.id}")
						val unitName = product.unit(Name, Abbreviation).getOrElse("unit")
						println("Please add a short description for this invoice item")
						println(s"Leaving this empty will yield: $productName")
						val description = StdIn.readNonEmptyLine().getOrElse(productName)
						println(s"How many units ($unitName) of $productName were sold? (default = 1.0)")
						println("Hint: Allows for decimal numbers")
						val amount = StdIn.read().doubleOr(1.0)
						println(s"What's the price of a single $unitName of $productName (without applying any taxes)?")
						val pricePerUnit = product.product.defaultUnitPrice match
						{
							case Some(default) =>
								println(s"Default = $default €/$unitName")
								StdIn.read().doubleOr(default)
							case None => StdIn.readIterator.flatMap { _.double }.next()
						}
						// Collects the information together
						(product, description, pricePerUnit, amount)
					}
				}
				else
					None
			}.takeWhile { _.isDefined }.toVector.flatten
			
			// Saves the invoice
			if (invoiceItemData.nonEmpty ||
				StdIn.ask("You didn't register any invoice items. Do you still want to save this invoice?"))
			{
				selectOrCreateBankAccount(userId, senderCompany.id).map { bankAccount =>
					// TODO: Should check whether reference code is a duplicate
					val invoice = InvoiceModel.insert(InvoiceData(senderCompany.details.id, recipientCompany.details.id,
						bankAccount.id, invoiceLanguage.id,
						ReferenceCode(userId, senderCompany.id, recipientCompany.id, Random.nextInt(1000)),
						duration, deliveryDate, Some(userId)))
					val invoiceItems = InvoiceItemModel.insert(invoiceItemData
						.map { case (product, description, pricePerUnit, amount) =>
							InvoiceItemData(invoice.id, product.id, description, pricePerUnit, amount) })
					
					// May print the invoice afterwards
					if (StdIn.ask("Do you want to export this invoice into a pdf?"))
					{
						// Creates full invoice data
						senderCompany.details.addressAccess.full.flatMap { senderAddress =>
							recipientCompany.details.addressAccess.full.map { recipientAddress =>
								val fullInvoiceItems = invoiceItems.zip(invoiceItemData)
									.map { case (item, (product, _, _, _)) => item + product }
								FullInvoice(invoice, senderCompany + senderAddress,
									recipientCompany + recipientAddress, bankAccount, fullInvoiceItems)
							}
						} match
						{
							case Some(invoice) => print(invoice)
							case None => println("Some required data was missing (inform the developer of this problem)")
						}
					}
					
					Some(invoice -> invoiceItems)
				}
			}
			else
				None
		}
	}
	
	
	// NESTED   ------------------------------
	
	private object PrintFields
	{
		val dateFormat = DateTimeFormatter.ofPattern("dd.MM.uuuu")
		
		val invoiceNumber = "invoice-index"
		val referenceCode = "reference-code"
		
		val date = "date"
		val duration = "pay-duration"
		val deadline = "payment-deadline"
		val delivery = "delivery-date"
		
		val totalPrice = "payment-total"
		val totalTax = "tax-total"
		val totalPriceTaxed = "payment-total-taxed"
		
		object Sender
		{
			val name = "sender-name"
			val yCode = "sender-y-code"
			val taxCode = "sender-tax-code"
			val address = "sender-address"
			val postalCode = "sender-postal-code"
			val iban = "sender-bank"
			val bic = "sender-bic"
			
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
			val name = "buyer-name"
			val yCode = "buyer-y-code"
			val id = "customer-code"
			val address = "buyer-address"
			val postalCode = "buyer-postal-code"
			
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
			val name = "name"
			val amount = "amount"
			val unit = "unit"
			val unitPrice = "unit-price"
			val price = "price"
			val taxPercent = "tax"
			val totalPrice = "price-taxed"
			
			def from(item: FullInvoiceItem, index: Int) =
			{
				val unitName = item.product.unit(Abbreviation, Name).getOrElse("")
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
			val price = invoice.totalPrice
			val tax = invoice.totalTax
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
				delivery -> invoice.productDeliveryDate.map(dateFormat.format).getOrElse(""),
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
