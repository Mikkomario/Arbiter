package vf.arbiter.command.app

import utopia.citadel.database.access.many.description.DbOrganizationDescriptions
import utopia.citadel.database.access.single.organization.DbOrganization
import utopia.citadel.model.enumeration.CitadelDescriptionRole.Name
import utopia.citadel.model.enumeration.StandardUserRole.Owner
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.StringExtensions._
import utopia.metropolis.model.cached.LanguageIds
import utopia.vault.database.Connection
import vf.arbiter.core.database.access.many.company.DbDetailedCompanies
import vf.arbiter.core.database.access.single.company.DbCompany
import vf.arbiter.core.database.access.single.location.{DbCounty, DbPostalCode, DbStreetAddress}
import vf.arbiter.core.model.combined.company.DetailedCompany
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
			// If exact match was not found, but others were, allows to select from those
			.orElse { if (existingOptions.isEmpty) None else
				ActionUtils.selectFrom(existingOptions.map { c => c -> c.nameAndYCode }, "companies") }
			// If not selected, allows to create a new company
			.orElse {
				if (StdIn.ask(s"Do you want to create '$nameSearch' as a new company (not owned)?"))
					Some(newCompany(userId, nameSearch))
				else
					None
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
			Some(newOwnCompany(ownerId, companyName))
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
							if (StdIn.ask(s"Do you want to insert a new company '$companyName' instead?"))
								Some(newOwnCompany(ownerId, companyName))
							else
								None
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
				val language = ActionUtils.forceSelectKnownLanguage()
				linkCompanyToNewOrganization(userId, company, language.id)
				Some(company)
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
					DbOrganization(organizationId).memberships.insert(userId, Owner.id, userId)
					company
				}
			}
		}
	}
	
	private def newOwnCompany(ownerId: Int, companyName: String)
	                         (implicit connection: Connection, languageIds: LanguageIds) =
	{
		println("What language the company name is in?")
		val language = ActionUtils.forceSelectKnownLanguage()
		// Inserts company information
		val company = newCompany(ownerId, companyName, isOwned = true)
		// Inserts organization information
		linkCompanyToNewOrganization(ownerId, company, language.id)
		company
	}
	
	private def newCompany(userId: Int, companyName: String, isOwned: Boolean = false)
	                      (implicit connection: Connection) =
	{
		val yCode = StdIn.readLineUntilNotEmpty("What's the identifier (Y-Tunnus) of the company?")
		val countyName = StdIn.readLineUntilNotEmpty("County?").capitalize
		val postalCodeInput = StdIn.readLineUntilNotEmpty("Postal code?")
		val streetName = StdIn.readLineUntilNotEmpty("Street name (without number)?").capitalize
		val houseNumber = StdIn.readLineUntilNotEmpty("Building number?")
		val stair = StdIn.readNonEmptyLine("Stair? (optional)")
		val roomNumber = StdIn.readNonEmptyLine("Room number? (optional)")
		val taxCode = StdIn.readNonEmptyLine("Tax code? (optional)")
		
		// Inserts address information
		val county = DbCounty.getOrInsert(countyName, Some(userId))
		val postalCode = DbPostalCode.getOrInsert(county.id, postalCodeInput, Some(userId))
		val address = DbStreetAddress.getOrInsert(
			StreetAddressData(postalCode.id, streetName, houseNumber, stair, roomNumber, Some(userId)))
		// Inserts company information
		DbCompany.insert(yCode, companyName, address.id, taxCode, Some(userId), isOfficial = isOwned)
	}
	
	private def linkCompanyToNewOrganization(ownerId: Int, company: DetailedCompany, companyNameLanguageId: Int)
	                                        (implicit connection: Connection) =
	{
		// Inserts organization information
		val organizationId = DbOrganization.insert(company.details.name, companyNameLanguageId, ownerId)
		// Inserts the user as an owner of that organization
		DbOrganization(organizationId).memberships.insert(ownerId, Owner.id, ownerId)
		// Registers a link between the company and the organization
		company.company.access.linkToOrganizationWithId(organizationId)
	}
}
