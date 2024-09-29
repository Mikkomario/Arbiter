package vf.arbiter.command.app

import utopia.citadel.model.enumeration.CitadelDescriptionRole.Name
import utopia.flow.collection.immutable.Empty
import utopia.flow.time.Now
import utopia.flow.util.NotEmpty
import utopia.flow.util.StringExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.view.immutable.caching.Lazy
import utopia.metropolis.model.cached.LanguageIds
import utopia.metropolis.model.partial.description.DescriptionData
import utopia.vault.database.Connection
import vf.arbiter.command.model.cached.SelectedLanguage
import vf.arbiter.core.database.access.many.company.DbCompanyProducts
import vf.arbiter.core.database.access.many.invoice.DbItemUnits
import vf.arbiter.core.database.access.single.company.{DbCompany, DbCompanyProduct}
import vf.arbiter.core.database.access.single.description.DbCompanyProductDescription
import vf.arbiter.core.database.access.single.invoice.DbItemUnit
import vf.arbiter.core.database.model.CoreDescriptionLinkModel
import vf.arbiter.core.database.model.company.CompanyProductModel
import vf.arbiter.core.model.combined.company.{DescribedCompanyProduct, FullCompanyProduct}
import vf.arbiter.core.model.combined.invoice.DescribedItemUnit
import vf.arbiter.core.model.enumeration.ArbiterDescriptionRoleId.Abbreviation
import vf.arbiter.core.model.partial.company.CompanyProductData

import scala.io.StdIn

/**
 * Provides interactive actions related to products
 *
 * @author Mikko Hilpinen
 * @since 29.09.2024, v1.5
 */
object ProductActions
{
	/**
	 * Pulls product unit data from the database.
	 * If some information is missing in the targeted language, asks the user to provide it.
	 * @param language Targeted language
	 * @param userId Id of the user requesting / using this information
	 * @param connection Implicit DB connection
	 * @param languageIds Implicit language ids
	 * @return All available units with proper descriptions
	 */
	def allUnitsIn(language: SelectedLanguage, userId: Int)
	              (implicit connection: Connection, languageIds: LanguageIds) =
	{
		DbItemUnits.described.filter { u => u.has(Name) || u.has(Abbreviation) }
			.sortBy { _.wrapped.categoryId }
			.map { u =>
				val (unitPlaceholderName, hasName, hasAbbreviation) = u.description(Name) match {
					case Some(nameDescription) =>
						val hasAbbreviation = u.description(Abbreviation).exists { _.languageId == language.id }
						if (nameDescription.languageId == language.id)
							(nameDescription.text, true, hasAbbreviation)
						else
							(nameDescription.text, false, hasAbbreviation)
					case None =>
						val abbreviation = u.description(Abbreviation).get
						(abbreviation.text, false, abbreviation.languageId == language.id)
				}
				// Case: Required data already exists
				if (hasName && hasAbbreviation)
					u
				// Case: Some data is missing => asks the user to fill it in
				else {
					val newName = if (hasName) None else StdIn.readNonEmptyLine(
						s"What's the name of $unitPlaceholderName in ${language.name}")
					val newAbbreviation = if (hasAbbreviation) None else
						StdIn.readNonEmptyLine(s"What's the abbreviation of ${
							newName.getOrElse(unitPlaceholderName)
						} in ${language.name}?")
					val newDescriptionData = (newName.map { _ -> Name.id } ++
						newAbbreviation.map { _ -> Abbreviation.id })
						.map { case (text, roleId) => DescriptionData(roleId, language.id, text, Some(userId)) }
						.toVector
					val newDescriptions = CoreDescriptionLinkModel.itemUnit.insert(u.id, newDescriptionData)
					// Replaces the existing descriptions with the new ones
					newDescriptions.foldLeft(u) { (u, newDescription) =>
						u.copy(descriptions = u.descriptions
							.filter { _.description.roleId != newDescription.description.roleId } + newDescription)
					}
				}
			}
	}
	
	/**
	 * Reads all available product information, including lazily initialized detailed information in the correct language
	 * @param userId Id of the user requesting / using this information
	 * @param companyId Id of the company the user represents
	 * @param language Targeted language
	 * @param units Information about the product units in the targeted language.
	 *              See [[allUnitsIn]].
	 * @param connection Implicit DB connection
	 * @param languageIds Implicit language ids
	 * @return All products, each coupled with lazily initialized full product information in the correct language
	 */
	def allProductsFor(userId: Int, companyId: Int, language: SelectedLanguage, units: Seq[DescribedItemUnit])
	                  (implicit connection: Connection, languageIds: LanguageIds) =
		DbCompany(companyId).products.described
			.sortBy { _.name }
			.map { p => p -> Lazy { ProductActions.fillDetails(userId, p, language, units) } }
	
	/**
	 * Creates a new product
	 * @param userId Id of the user creating this product
	 * @param companyId Id of the company using this product
	 * @param language Language in which this product is used
	 * @param units Available unit information
	 * @param connection Implicit DB connection
	 * @return Newly created product. None if user canceled the product-creation.
	 */
	def create(userId: Int, companyId: Int, language: SelectedLanguage, units: Seq[DescribedItemUnit])
	          (implicit connection: Connection) =
	{
		val retryPrompt = "This information is required. Leaving empty will cancel this process."
		StdIn.readNonEmptyLine(s"What's the name of this new product in ${language.name}?", retryPrompt).flatMap { name =>
			println("What's the unit in which this product is sold (select from below)")
			StdIn.selectFrom(units.map { u => u -> u.apply(Name, Abbreviation).nonEmptyOrElse("?") })
				.map { selectedUnit =>
					val defaultPrice = StdIn.read(
						s"What's the default price (€) of this product for one ${
							selectedUnit.name }? (optional)").double
					val taxModifier = StdIn.read(
							"What's the VAT percentage applied for this product? (default = 25.5%)")
						.double.map { _ / 100.0 }.getOrElse(0.255)
					
					// Inserts the product and it's name to the database
					val product = CompanyProductModel.insert(CompanyProductData(companyId,
						selectedUnit.id, defaultPrice, taxModifier, Some(userId)))
					val nameDescription = DbCompanyProductDescription.linkModel
						.insert(product.id, DescriptionData(Name.id, language.id, name, Some(userId)))
					val describedProduct = DescribedCompanyProduct(product, Set(nameDescription))
					
					FullCompanyProduct(describedProduct, selectedUnit)
				}
		}
	}
	
	/**
	 * Converts a described product into a full product, ensuring that it has a name and a description
	 * in the targeted language
	 * @param userId Id of the user creating / using this product
	 * @param product Product to describe
	 * @param language Language in which the product needs to be described
	 * @param units Available unit information
	 * @param connection Implicit DB connection
	 * @param languageIds Implicit language ids
	 * @return A fully detailed product
	 */
	def fillDetails(userId: Int, product: DescribedCompanyProduct, language: SelectedLanguage,
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
	
	/**
	 * Archives 0-n products
	 * @param companyId Id of the targeted company
	 * @param connection Implicit DB connection
	 * @param languageIds Implicit language ids
	 */
	def archive(companyId: Int)(implicit connection: Connection, languageIds: LanguageIds) = {
		var products = DbCompanyProducts.ofCompany(companyId).described
		var shouldContinue = true
		while (shouldContinue && products.nonEmpty) {
			println("Select the next product to archive. Empty cancels.")
			StdIn.selectFrom(products.map { p => p -> p.name }) match {
				case Some(product) =>
					DbCompanyProduct(product.id).discontinuedAfter = Now
					products = products.filterNot { _.id == product.id }
					println(s"${ product.name } archived")
				
				case None => shouldContinue = false
			}
		}
	}
	
	/**
	 * Changes the VAT percentage of a certain group of products.
	 * Useful in situations where a country's VAT regulations change.
	 * @param from The targeted VAT range [0,1]
	 * @param to Assigned new VAT ratio [0,1]
	 * @param userId Id of the user performing this change
	 * @param companyId Id of the company whose products are targeted
	 * @param connection Implicit DB connection
	 * @return New versions of the company's products (from the targeted VAT range only)
	 */
	def changeVat(from: Double, to: Double, userId: Int, companyId: Int)(implicit connection: Connection) = {
		NotEmpty(DbCompanyProducts.ofCompany(companyId).withTaxModifier(from).pull) match {
			case Some(products) =>
				if (StdIn.ask(s"Are you sure you want to change the VAT% of ${ products.size } products from ${
					from * 100 } to ${ to * 100 }?"))
				{
					DbCompanyProducts(products.map { _.id }).discontinuedAfters = Now
					CompanyProductModel.insert(products.map { _.data.copy(taxModifier = to, creatorId = Some(userId)) })
				}
				else {
					println("Product-modifying canceled")
					products
				}
			case None =>
				println(s"There are no products listed with ${ from * 100 }% tax rate")
				Empty
		}
	}
}
