package vf.arbiter.command.app

import utopia.citadel.database.access.id.single.DbLanguageId
import utopia.citadel.database.access.many.description.DbDescriptions
import utopia.citadel.database.access.single.organization.DbOrganization
import utopia.citadel.model.enumeration.StandardUserRole.Owner
import utopia.flow.util.console.ConsoleExtensions._
import utopia.vault.database.Connection
import vf.arbiter.core.database.access.single.company.DbCompany
import vf.arbiter.core.database.access.single.location.{DbCounty, DbPostalCode, DbStreetAddress}
import vf.arbiter.core.database.model.company.CompanyModel
import vf.arbiter.core.model.partial.company.CompanyData
import vf.arbiter.core.model.partial.location.StreetAddressData
import vf.arbiter.core.model.stored.company.Company

import scala.io.StdIn

/**
 * Contains interactive functions related to company interactions
 * @author Mikko Hilpinen
 * @since 10.10.2021, v0.1
 */
object CompanyActions
{
	/**
	 * Starts a new company or joins an existing company
	 * @param ownerId Id of the user / company owner
	 * @param companyName Name of the new / existing company (or part of a name)
	 * @param connection Implicit DB Connection
	 * @return Company that was linked or joined
	 */
	def startOrJoin(ownerId: Int, companyName: String)(implicit connection: Connection) =
		DbCompany.matchingName(companyName) match
		{
			case Some(company) =>
				println(s"Found existing company: ${company.name} (${company.yCode})")
				// Checks whether the user is already a member of that company
				val memberships = company.access.memberships
				if (memberships.exists { _.userId == ownerId })
				{
					println("You're already a member of this company")
					Some(company)
				}
				else if (companyName == company.name ||
					StdIn.ask("Do you want to join this company?", default = true))
				{
					// Finds the organizations linked with this company
					val organizationIds = memberships.map { _.organizationId }.toSet
					if (organizationIds.isEmpty)
					{
						val languageCode = StdIn.read(
							"What language the company name is in? (default = en)").stringOr("en")
						linkCompanyToNewOrganization(ownerId, company, languageCode)
					}
					else
					{
						// Determines, which organization will be linked
						val selectedOrganizationId =
						{
							// Case: Only one option => uses that
							if (organizationIds.size == 1)
								organizationIds.head
							else
							{
								// Reads descriptions of those organizations
								val organizationDescriptions =
									DbDescriptions.ofOrganizationsWithIds(organizationIds).pull
								// Case: No descriptions available => selects randomly
								if (organizationDescriptions.isEmpty)
									organizationIds.head
								// Case: Only one of the organizations is described => selects that one
								else if (organizationDescriptions.map { _.targetId }.toSet.size == 1)
								{
									println("Links to organization: " +
										organizationDescriptions.map { _.description.text }.mkString(", "))
									organizationDescriptions.head.targetId
								}
								// Case: Multiple choices => Lets the user select
								else
								{
									val options = organizationDescriptions.groupMap { _.targetId } { _.description }
										.toVector
									println(s"Found ${options.size} options. Please select from the list below:")
									options.indices.foreach { index =>
										println(s"${index + 1}: ${
											options(index)._2.sortBy { _.roleId }.map { _.text }.mkString(", ")}")
									}
									println("Type the number of the targeted company")
									val selectedIndex = StdIn.readIterator.flatMap { _.int }
										.find { i => i > 0 && i <= options.size }.get
									options(selectedIndex)._1
								}
							}
						}
						// Adds the user to that organization
						DbOrganization(selectedOrganizationId).memberships.insert(ownerId, Owner.id, ownerId)
					}
					Some(company)
				}
				else if (StdIn.ask(s"Do you want to insert $companyName as a new company instead?"))
					Some(newCompany(ownerId, companyName))
				else
					None
			case None => Some(newCompany(ownerId, companyName))
		}
	
	private def newCompany(ownerId: Int, companyName: String)(implicit connection: Connection) =
	{
		val languageCode = StdIn.read("What language the company name is in? (default = en)")
			.stringOr("en")
		val yCode = StdIn.readLineUntilNotEmpty("What's the identifier (Y-Tunnus) of your company?")
		val countyName = StdIn.readLineUntilNotEmpty("County?").capitalize
		val postalCodeInput = StdIn.readLineUntilNotEmpty("Postal code?")
		val streetName = StdIn.readLineUntilNotEmpty("Street name?").capitalize
		val houseNumber = StdIn.readLineUntilNotEmpty("Building number?")
		val stair = StdIn.readNonEmptyLine("Stair? (optional)")
		val roomNumber = StdIn.readNonEmptyLine("Room number? (optional)")
		val taxCode = StdIn.readNonEmptyLine("Tax code? (optional)")
		
		// Inserts address information
		val county = DbCounty.withNameOrInsert(countyName)
		val postalCode = DbPostalCode.getOrInsert(county.id, postalCodeInput)
		val address = DbStreetAddress.getOrInsert(
			StreetAddressData(postalCode.id, streetName, houseNumber, stair, roomNumber))
		// Inserts company information
		val company = CompanyModel.insert(CompanyData(yCode, companyName, address.id, taxCode))
		// Inserts organization information
		linkCompanyToNewOrganization(ownerId, company, languageCode)
		
		company
	}
	
	private def linkCompanyToNewOrganization(ownerId: Int, company: Company, languageCode: String)
	                                        (implicit connection: Connection) =
	{
		// Inserts organization information
		val languageId = DbLanguageId.forIsoCode(languageCode).getOrInsert()
		val organizationId = DbOrganization.insert(company.name, languageId, ownerId)
		// Inserts the user as an owner of that organization
		DbOrganization(organizationId).memberships.insert(ownerId, Owner.id, ownerId)
		// Registers a link between the company and the organization
		company.access.linkToOrganizationWithId(organizationId)
	}
}
