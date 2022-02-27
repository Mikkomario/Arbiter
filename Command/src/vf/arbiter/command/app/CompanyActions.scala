package vf.arbiter.command.app

import utopia.citadel.database.access.many.description.DbOrganizationDescriptions
import utopia.citadel.database.access.single.language.DbLanguage
import utopia.citadel.database.access.single.organization.DbOrganization
import utopia.citadel.database.access.single.user.DbUser
import utopia.citadel.database.model.description.DescriptionModel
import utopia.citadel.model.enumeration.CitadelDescriptionRole.Name
import utopia.citadel.model.enumeration.CitadelUserRole.Owner
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.StringExtensions._
import utopia.metropolis.model.cached.LanguageIds
import utopia.metropolis.model.partial.description.DescriptionData
import utopia.vault.database.Connection
import vf.arbiter.core.database.ArbiterDbExtensions._
import vf.arbiter.core.database.access.many.company.DbDetailedCompanies
import vf.arbiter.core.database.access.many.invoice.DbItemUnits
import vf.arbiter.core.database.access.single.company.DbCompany
import vf.arbiter.core.database.access.single.location.{DbCounty, DbPostalCode, DbStreetAddress}
import vf.arbiter.core.database.model.CoreDescriptionLinkModel
import vf.arbiter.core.database.model.company.{CompanyDetailsModel, CompanyProductModel}
import vf.arbiter.core.model.combined.company.DetailedCompany
import vf.arbiter.core.model.enumeration.ArbiterDescriptionRoleId.Abbreviation
import vf.arbiter.core.model.partial.company.{CompanyDetailsData, CompanyProductData}
import vf.arbiter.core.model.partial.location.StreetAddressData

import scala.io.StdIn

/**
 * Contains interactive functions related to company interactions
 * @author Mikko Hilpinen
 * @since 10.10.2021, v0.1
 */
object CompanyActions
{
	/**
	 * Searches for a company based on its name
	 * @param nameSearch Search string
	 * @param connection Implicit DB Connection
	 * @return Found company. None if no company was found or selected.
	 */
	def findAndSelectOne(nameSearch: String)(implicit connection: Connection) =
	{
		val options = DbDetailedCompanies.matchingName(nameSearch)
		options.find { _.details.name ~== nameSearch }
			.orElse { ActionUtils.selectFrom(
				options.map { c => c -> c.nameAndYCode }.sortBy { _._2 }, "companies") }
	}
	
	/**
	 * Finds or creates a company - the company will not be registered as owned company
	 * @param nameSearch Company name or part of an existing company name
	 * @param connection Implicit DB Connection
	 * @return Found or created company
	 */
	def findOrCreateOne(userId: Int, nameSearch: String)(implicit connection: Connection) =
	{
		// Finds existing companies matching that name
		val existingOptions = DbDetailedCompanies.matchingName(nameSearch)
		// Checks for an exact name match
		existingOptions.find { _.details.name ~== nameSearch }
			// Allows to select from matches or to create a new company
			.orElse {
				ActionUtils.selectOrInsert(existingOptions.map { c => c -> c.nameAndYCode }, "company") {
					newCompany(userId, nameSearch) }
			}
	}
	
	/**
	 * Selects one of the user's linked companies
	 * @param userId Id of the user
	 * @param connection Implicit DB Connection
	 * @return Selected company. None if no company was selected.
	 */
	def selectOneFromOwn(userId: Int)(implicit connection: Connection) =
		ActionUtils.selectFrom(DbDetailedCompanies.linkedWithUserWithId(userId).map { c => c -> c.details.name },
			"companies")
	
	/**
	 * Offers the user a chance to join a company based on a search
	 * @param userId Id of the user in question
	 * @param nameSearch Searched company name
	 * @param connection Implicit DB connection
	 * @return Joined company. None if no company was joined.
	 */
	def findAndJoinOne(userId: Int, nameSearch: String)
	                  (implicit connection: Connection, languageIds: LanguageIds) =
		findAndSelectOne(nameSearch).map { company =>
			join(userId, company)
			company
		}
	
	/**
	 * Allows the user to register a new company for themselves or to select from one of their own
	 * @param ownerId Id of the owner / user
	 * @param companyName Name of the new company (call-by-name)
	 * @param connection Implicit DB Connection
	 * @return Selected, registered or joined company
	 */
	def startOrSelectFromOwn(ownerId: Int, companyName: => String)
	                        (implicit connection: Connection, languageIds: LanguageIds) =
	{
		// Checks whether the user already has a company / companies
		val existingOptions = DbDetailedCompanies.linkedWithUserWithId(ownerId)
		// If so, asks whether the user still wants to create one, in which case moves to company creation / joining
		if (existingOptions.isEmpty || StdIn.ask(s"You already have ${
			existingOptions.size} companies. Are you sure you want to create a new one?"))
			CompanyActions.startOrJoin(ownerId, companyName)
		// Allows the user to select from existing companies
		else
			ActionUtils.selectFrom(existingOptions.map { c => c -> c.details.name }, "companies", "use")
	}
	
	/**
	 * Starts a new company or joins an existing company
	 * @param ownerId Id of the user / company owner
	 * @param companyName Name of the new / existing company (or part of a name)
	 * @param connection Implicit DB Connection
	 * @return Company that was linked or joined
	 */
	def startOrJoin(ownerId: Int, companyName: String)(implicit connection: Connection, languageIds: LanguageIds) =
	{
		val companyOptions = DbDetailedCompanies.matchingName(companyName)
		if (companyOptions.isEmpty)
			newOwnCompany(ownerId, companyName)
		else
			companyOptions.find { _.details.name ~== companyName } match
			{
				case Some(exactMatch) => join(ownerId, exactMatch)
				case None =>
					ActionUtils.selectFrom(companyOptions.map { c => c -> c.nameAndYCode }.sortBy { _._2 },
						"companies", "join") match
					{
						case Some(selected) => join(ownerId, selected)
						case None =>
							if (StdIn.ask(s"Do you want to add a new company '$companyName' instead?"))
								newOwnCompany(ownerId, companyName)
							else
								None
					}
			}
	}
	
	/**
	 * Edits a company's information
	 * @param userId Id of the user doing the editing
	 * @param companyName Targeted company's name or part of that company's name
	 * @param connection Implicit DB Connection
	 */
	def edit(userId: Int, companyName: String)(implicit connection: Connection) = {
		// Finds the company to edit
		val options = DbDetailedCompanies.matchingName(companyName)
		if (options.isEmpty)
			println(s"No company matching '$companyName'")
		else
			options.find { _.details.name ~== companyName }
				.orElse { ActionUtils.selectFrom(options.map { c => c -> c.nameAndYCode },
					"companies", "edit", skipQuestion = true) }
				.foreach { company =>
					// Asks the user to provide new company information
					val oldAddress = company.details.addressAccess.full.get
					
					println("Please provide new company information. Leaving a field empty will keep the previous value.")
					val newName = StdIn.readNonEmptyLine(s"New company name? (default: ${company.details.name})")
					val newCounty = StdIn.readNonEmptyLine(s"New county? (default: ${oldAddress.county.name})")
						.map { _.capitalize }
					val newPostal = StdIn.readNonEmptyLine(
						s"New postal code? (default: ${oldAddress.postalCode.number})")
					val newStreet = StdIn.readNonEmptyLine(s"New street name (without number) (default: ${
						oldAddress.streetName})")
						.map { _.capitalize }
					val newBuilding = StdIn.readNonEmptyLine(
						s"New building number? (default: ${oldAddress.buildingNumber})")
					val newStair = StdIn.readNonEmptyLine(s"New stair? (default: ${oldAddress.stair})")
					val newRoom = StdIn.readNonEmptyLine(s"New room number? (default: ${oldAddress.roomNumber})")
					val newTaxCode = StdIn.readNonEmptyLine(s"New tax code? (default: ${company.details.taxCode})")
					
					// Inserts the new information
					val countyId = newCounty match {
						case Some(newCountyName) => DbCounty.getOrInsert(newCountyName, Some(userId)).id
						case None => oldAddress.county.id
					}
					val postalId = {
						if (countyId != oldAddress.county.id || newPostal.nonEmpty)
							DbPostalCode.getOrInsert(countyId, newPostal.getOrElse(oldAddress.postalCode.number),
								Some(userId)).id
						else
							oldAddress.postalCodeId
					}
					val addressId = {
						if (postalId != oldAddress.postalCodeId ||
							Vector(newStreet, newBuilding, newStair, newRoom).exists { _.nonEmpty })
							DbStreetAddress.getOrInsert(StreetAddressData(
								postalId, newStreet.getOrElse(oldAddress.streetName),
								newBuilding.getOrElse(oldAddress.buildingNumber), newStair.orElse(oldAddress.stair),
								newRoom.orElse(oldAddress.roomNumber), Some(userId))).id
						else
							oldAddress.id
					}
					if (addressId != oldAddress.id || newName.nonEmpty || newTaxCode.nonEmpty) {
						val isOfficial = DbUser(userId).isMemberOfCompanyWithId(company.id)
						company.details.access.deprecatedAfter = Now
						CompanyDetailsModel.insert(CompanyDetailsData(
							company.id, newName.getOrElse(company.details.name), addressId,
							newTaxCode.orElse(company.details.taxCode), Some(userId), isOfficial = isOfficial))
					}
				}
	}
	
	/**
	 * Chooses and edits an existing company product
	 * @param userId Id of the user doing the editing
	 * @param companyId Id of the company whose products are being edited
	 * @param connection Implicit DB Connection
	 * @param languageIds Implicit language ids
	 */
	def editProduct(userId: Int, companyId: Int)(implicit connection: Connection, languageIds: LanguageIds) =
	{
		// Finds the product to edit
		val products = DbCompany(companyId).products.described
		ActionUtils.selectFrom(products.map { p => p -> p(Name).getOrElse(s"Unnamed product #${p.id}") },
			"products", "edit", skipQuestion = true).foreach { product =>
			// Reads product unit data
			val unit = product.wrapped.unitAccess.describedWith(Name, Abbreviation)
			// May edit the product name
			val newName = {
				if (product.has(Name) && StdIn.ask("Do you want to edit this product's name?"))
				{
					val productNames = product.access.descriptions.withRole(Name).inAllPreferredLanguages.pull
					println("Choose the product name to edit")
					ActionUtils.selectFrom(productNames.map { link => link -> link.description.text })
						.flatMap { nameToReplace =>
							val language = DbLanguage(nameToReplace.description.languageId).withDescription(Name)
							val languageName = language match
							{
								case Some(language) => language.descriptionOrCode(Name)
								case None => "Language " + nameToReplace.description.languageId.toString
							}
							StdIn.readNonEmptyLine(s"What's the new name of this product in $languageName?")
								.map { nameToReplace -> _ }
						}
				}
				else
					None
			}
			// May edit product unit
			val currentUnitName = unit match
			{
				case Some(unit) => unit(Name, Abbreviation).getOrElse(s"Unnamed unit ${unit.id}")
				case None => s"Unnamed unit ${product.wrapped.unitId}"
			}
			val newUnit = {
				if (StdIn.ask(
					s"Do you want to edit the unit in which this product is sold (currently $currentUnitName)?"))
				{
					val units = DbItemUnits.described.filter { u => u.has(Name) || u.has(Abbreviation) }
					ActionUtils.selectFrom(units.map { u => u -> u(Name, Abbreviation).get }, "units",
						skipQuestion = true)
				}
				else
					None
			}
			// May edit default price per unit
			val updatedUnitName = newUnit match
			{
				case Some(unit) => unit(Name, Abbreviation).get
				case None => currentUnitName
			}
			val newDefaultPrice = StdIn.readValidOrEmpty(
				s"What's the default price of this product (€/$updatedUnitName)? (default = ${
					product.wrapped.defaultUnitPrice match {
					case Some(price) => s"$price €/$updatedUnitName"
					case None => "No default price"
				}})") { _.double match
			{
				case Some(d) => Right(d)
				case None => Left("Not a valid number. Please try again")
			} }.filterNot { d => product.wrapped.defaultUnitPrice.contains(d) }
			// May edit the tax modifier
			val newTaxMod = StdIn.readValidOrEmpty(s"What's the tax % applied to this product? (default = ${
				product.wrapped.taxModifier * 100})") { _.double match
			{
				case Some(d) => Right(d / 100.0)
				case None => Left("Not a valid number. Please try again.")
			} }
			
			// Case: Not modified
			if (newName.isEmpty && newUnit.isEmpty && newDefaultPrice.isEmpty && newTaxMod.isEmpty)
				println("Product not modified")
			// Case: Requires a new product
			else if (newUnit.isDefined || newTaxMod.isDefined)
			{
				if (StdIn.ask("Existing product will be discontinued and a new product created. Is this okay?",
					default = true))
				{
					// Discontinues the existing product
					product.access.discontinuedAfter = Now
					// Inserts a new product
					val newProduct = CompanyProductModel.insert(CompanyProductData(companyId,
						newUnit.map { _.id }.getOrElse(product.wrapped.unitId),
						newDefaultPrice.orElse(product.wrapped.defaultUnitPrice),
						newTaxMod.getOrElse(product.wrapped.taxModifier), Some(userId)))
					// Copies descriptions for the new product & inserts new name, if necessary
					val oldProductDescriptions = product.access.descriptions.pull
					val copiedDescriptions = newName match
					{
						case Some((linkToReplace, _)) => oldProductDescriptions.filter { _.id != linkToReplace.id }
						case None => oldProductDescriptions
					}
					CoreDescriptionLinkModel.companyProduct
						.insert(newProduct.id, copiedDescriptions.map { _.description.data })
					newName.foreach { case (linkToReplace, newName) =>
						CoreDescriptionLinkModel.companyProduct.insert(newProduct.id,
							DescriptionData(Name.id, linkToReplace.description.languageId, newName, Some(userId)))
					}
					println("Product replaced")
				}
			}
			// Case: May update the existing product
			else
			{
				// Updates new default price if necessary
				newDefaultPrice.foreach { newDefaultPrice => product.access.defaultUnitPrice = newDefaultPrice }
				// Adds a new name if necessary
				newName.foreach { case (descriptionToReplace, newName) =>
					DescriptionModel.deprecateId(descriptionToReplace.id)
					CoreDescriptionLinkModel.companyProduct.insert(product.id,
						DescriptionData(Name.id, descriptionToReplace.languageId, newName, Some(userId)))
				}
				println("Product updated")
			}
		}
	}
	
	private def join(userId: Int, company: DetailedCompany)
	                (implicit connection: Connection, languageIds: LanguageIds): Option[DetailedCompany] =
	{
		// Checks whether the user is already a member of that company
		val memberships = company.company.access.memberships
		if (memberships.exists { _.userId == userId })
		{
			println("You're already a member of this company")
			Some(company)
		}
		else
		{
			// Finds the organizations linked with this company
			val organizationIds = memberships.map { _.organizationId }.toSet
			if (organizationIds.isEmpty)
			{
				println(s"What language the company name (${company.details.name}) is in?")
				UserActions.selectOrAddKnownLanguage(userId).map { language =>
					linkCompanyToNewOrganization(userId, company, language.id)
					company
				}
			}
			else
			{
				// Determines, which organization will be linked
				val selectedOrganizationId =
				{
					// Case: Only one option => uses that
					if (organizationIds.size == 1)
						Some(organizationIds.head)
					else
					{
						// Reads descriptions of those organizations
						val organizationNames = DbOrganizationDescriptions(organizationIds)
							.withRoleIdInPreferredLanguages(Name.id)
							.view.mapValues { _.description.text }.toMap
						// Selects one of them
						ActionUtils.selectFrom(organizationIds.toVector
							.flatMap { orgId => organizationNames.get(orgId).map { orgId -> _ } },
							"organizations", "link")
					}
				}
				// Adds the user to that organization
				selectedOrganizationId.map { organizationId =>
					DbOrganization(organizationId).addMember(userId, Owner.id, Some(userId))
					company
				}
			}
		}
	}
	
	private def newOwnCompany(ownerId: Int, companyName: String)
	                         (implicit connection: Connection, languageIds: LanguageIds) =
	{
		println("What language the company name is in?")
		UserActions.selectOrAddKnownLanguage(ownerId).flatMap { language =>
			// Inserts company information
			newCompany(ownerId, companyName, isOwned = true).map { company =>
				// Inserts organization information
				linkCompanyToNewOrganization(ownerId, company, language.id)
				company
			}
		}
	}
	
	private def newCompany(userId: Int, companyName: String, isOwned: Boolean = false)
	                      (implicit connection: Connection) =
	{
		val retryPrompty = "Company creation can't be continued without this value. Write a value or leave empty to cancel company creation."
		StdIn.readNonEmptyLine("What's the identifier (Y-Tunnus) of the company?", retryPrompty).flatMap { yCode =>
			StdIn.readNonEmptyLine("County?", retryPrompty).map { _.capitalize }.flatMap { countyName =>
				StdIn.readNonEmptyLine("Postal code?", retryPrompty).flatMap { postalCodeInput =>
					StdIn.readNonEmptyLine("Street name (without number)?", retryPrompty).map { _.capitalize }
						.flatMap { streetName =>
							StdIn.readNonEmptyLine("Building number?", retryPrompty).map { houseNumber =>
								val stair = StdIn.readNonEmptyLine("Stair? (optional)")
								val roomNumber = StdIn.readNonEmptyLine("Room number? (optional)")
								val taxCode = StdIn.readNonEmptyLine("Tax code? (optional)")
								
								// Inserts address information
								val county = DbCounty.getOrInsert(countyName, Some(userId))
								val postalCode = DbPostalCode.getOrInsert(county.id, postalCodeInput, Some(userId))
								val address = DbStreetAddress.getOrInsert(StreetAddressData(postalCode.id, streetName,
									houseNumber, stair, roomNumber, Some(userId)))
								// Inserts company information
								DbCompany.insert(yCode, companyName, address.id, taxCode, Some(userId),
									isOfficial = isOwned)
							}
						}
				}
			}
		}
	}
	
	private def linkCompanyToNewOrganization(ownerId: Int, company: DetailedCompany, companyNameLanguageId: Int)
	                                        (implicit connection: Connection) =
	{
		// Creates a new organization
		val (organization, _) = DbOrganization.found(company.details.name, companyNameLanguageId, ownerId)
		// Registers a link between the company and the organization
		company.company.access.linkToOrganizationWithId(organization.id)
	}
}
