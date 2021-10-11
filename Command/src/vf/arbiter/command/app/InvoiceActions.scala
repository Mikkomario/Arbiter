package vf.arbiter.command.app

import utopia.citadel.database.access.id.single.DbLanguageId
import utopia.flow.datastructure.mutable.{Pointer, PointerWithEvents}
import utopia.flow.time.Days
import utopia.flow.util.console.ConsoleExtensions._
import utopia.metropolis.model.partial.description.DescriptionData
import utopia.vault.database.Connection
import vf.arbiter.core.database.access.many.description.{DbCompanyProductDescriptions, DbItemUnitDescriptions}
import vf.arbiter.core.database.access.single.description.DbCompanyProductDescription
import vf.arbiter.core.database.model.invoice.{CompanyProductModel, InvoiceModel}
import vf.arbiter.core.model.enumeration.ArbiterDescriptionRoleId
import vf.arbiter.core.model.partial.invoice.{CompanyProductData, InvoiceData, InvoiceItemData}
import vf.arbiter.core.model.stored.company.Company
import vf.arbiter.core.model.stored.invoice.CompanyProduct

import scala.io.StdIn

/**
 * Contains interactive invoice-related actions
 * @author Mikko Hilpinen
 * @since 11.10.2021, v0.1
 */
object InvoiceActions
{
	private def nameRoleId = ArbiterDescriptionRoleId.name
	
	/*
	When creating an invoice, we need following information:
		- Origin company
		- Target company
		- Duration days
		- Item delivery date (opt)
		- Product lines
			- Product
				- Name
				- Unit
				- Default price
				- Tax %
			- Description
			- Amount
			- Price per unit
	 */
	def create(userId: Int, company: Company)(implicit connection: Connection) =
	{
		// Selects the target company
		println("Which company this invoice is for?")
		println("Hint: If searching for an existing company, write a part of that company's name")
		StdIn.readNonEmptyLine().flatMap { CompanyActions.findOrCreateOne(_) }
			.map { targetCompany =>
				// Asks for basic information
				val duration = Days(StdIn.read(
					"How many days does the company have time to pay this bill? (default = 30)")
					.intOr(30))
				println("When were the services or items delivered for the customer?")
				println("Allowed formats: YYYY-MM-DD and DD.MM.YYYY")
				println("Leave empty if not applicable")
				val deliveryDate = StdIn.read().localDate
				
				// Prepares information for the next phase
				val unitNames = DbItemUnitDescriptions.all.forRoleWithId(nameRoleId).pull
					.map { dl => dl.targetId -> dl.description.text }.toMap
				val availableUnitIds = unitNames.keySet.toVector.sorted
				
				var existingProducts = company.access.products.pull
				var productNames = DbCompanyProductDescriptions(existingProducts.map { _.id }.toSet)
					.forRoleWithId(nameRoleId).pull
					.map { dl => dl.targetId -> dl.description.text }.toMap
				
				// Creates / prepares the invoice items
				val lastProductPointer = new PointerWithEvents[Option[CompanyProduct]](None)
				lastProductPointer.addListener { _.newValue.flatMap { p => productNames.get(p.id) }
					.foreach { n => println(s"Using product $n for the next invoice item") } }
				// Collected info: product id + description + amount + price per unit
				val invoiceItemData = Iterator.iterate(1) { _ + 1 }.map { index =>
					// Asks whether to stop iterating
					if (index <= 1 || StdIn.ask("Do you want to add another item to this invoice?"))
					{
						// Selects the product to use
						val product: Option[CompanyProduct] = lastProductPointer.value
							// Option A: Use same product as for the last line
							.filter { product =>
								StdIn.ask(s"Is the next item of the same product (${
									productNames.getOrElse(product.id, s"Unnamed product #${product.id}") })?")
							}
							// Option B: Select from existing products
							.orElse {
								if (existingProducts.nonEmpty)
									ActionUtils.selectFrom(existingProducts.map { p =>
										p -> productNames.getOrElse(p.id, s"Unnamed product #${p.id}") },
										"products", "refer to")
								else
									None
							}
							// Option C: Create a new product
							.orElse {
								if (StdIn.ask("Is it okay to create a new product? (no quits adding invoice items)",
									default = true))
									StdIn.readNonEmptyLine("Please name the new product").map { name =>
										val languageCode = StdIn.readNonEmptyLine(
											"What language did you use? (default = en)").getOrElse("en")
										println("What's the unit in which this product is sold (select from below)")
										val unitId = ActionUtils.forceSelectFrom(
											availableUnitIds.map { unitId => unitId -> unitNames(unitId) })
										val defaultPrice = StdIn.read(
											s"What's the default price of this product for one ${
												unitNames(unitId)}? (optional)").double
										val taxModifier = StdIn.read(
											"What's the tax percentage applied for this product? (default = 24%)")
											.double.map { _ / 100.0 }.getOrElse(24.0)
										
										// Inserts the product and it's name to the database
										val product = CompanyProductModel.insert(CompanyProductData(company.id, unitId,
											defaultPrice, taxModifier))
										DbCompanyProductDescription.model.insert(product.id, DescriptionData(nameRoleId,
											DbLanguageId.forIsoCode(languageCode).getOrInsert(), name, Some(userId)))
										
										// Adds this product to existing options
										existingProducts :+= product
										productNames += product.id -> name
										
										product
									}
								else
									None
							}
						
						lastProductPointer.value = product
						product.map { product =>
							// Collects invoice item information
							val productName = productNames.getOrElse(product.id, s"Unnamed product #${product.id}")
							val unitName = unitNames(product.unitId)
							println("Please add a short description for this invoice item")
							println(s"Leaving this empty will yield: $productName")
							val description = StdIn.readNonEmptyLine().getOrElse(productName)
							println(s"How many units ($unitName) of $productName were sold? (default = 1)")
							println("Hint: Allows for decimal numbers")
							val amount = StdIn.read().doubleOr(1.0)
							println(s"What's the price of a single $unitName of $productName (without applying any taxes)?")
							val pricePerUnit = product.defaultUnitPrice match
							{
								case Some(default) =>
									println(s"Default = $default €/$unitName")
									StdIn.read().doubleOr(default)
								case None => StdIn.readIterator.flatMap { _.double }.next()
							}
							// Collects the information together
							(product.id, description, amount, pricePerUnit)
						}
					}
					else
						None
				}.takeWhile { _.isDefined }.toVector.flatten
				
				// Saves the invoice
				if (invoiceItemData.nonEmpty ||
					StdIn.ask("You didn't register any invoice items. Do you still want to save this invoice?"))
				{
					// TODO: Continue
					// val invoice = InvoiceModel.insert(InvoiceData())
				}
			}
	}
}
