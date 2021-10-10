package vf.arbiter.command.app

import utopia.citadel.database.access.id.single.DbLanguageId
import utopia.citadel.database.access.many.description.DbDescriptions
import utopia.citadel.database.access.single.organization.DbOrganization
import utopia.citadel.model.enumeration.StandardUserRole.Owner
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.StringExtensions._
import utopia.vault.database.Connection
import vf.arbiter.core.database.access.many.company.DbCompanies
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
	 * Searches for a company based on its name
	 * @param nameSearch Search string
	 * @param connection Implicit DB Connection
	 * @return Found company. None if no company was found or selected.
	 */
	def findAndSelectOne(nameSearch: String)(implicit connection: Connection) =
	{
		val options = DbCompanies.matchingName(nameSearch)
		options.find { _.name ~== nameSearch }
			.orElse { selectFrom(options.map { c => c -> s"${c.name} (${c.yCode})" }.sortBy { _._2 }) }
	}
	
	/**
	 * Selects one of the user's linked companies
	 * @param userId Id of the user
	 * @param connection Implicit DB Connection
	 * @return Selected company. None if no company was selected.
	 */
	def selectOneFromOwn(userId: Int)(implicit connection: Connection) =
		selectFrom(DbCompanies.linkedWithUserWithId(userId).map { c => c -> c.name })
	
	/**
	 * Offers the user a chance to join a company based on a search
	 * @param userId Id of the user in question
	 * @param nameSearch Searched company name
	 * @param connection Implicit DB connection
	 * @return Joined company. None if no company was joined.
	 */
	def findAndJoinOne(userId: Int, nameSearch: String)(implicit connection: Connection) =
		findAndSelectOne(nameSearch).map { company =>
			join(userId, company)
			company
		}
	
	/**
	 * Starts a new company or joins an existing company
	 * @param ownerId Id of the user / company owner
	 * @param companyName Name of the new / existing company (or part of a name)
	 * @param connection Implicit DB Connection
	 * @return Company that was linked or joined
	 */
	def startOrJoin(ownerId: Int, companyName: String)(implicit connection: Connection) =
	{
		val companyOptions = DbCompanies.matchingName(companyName)
		if (companyOptions.isEmpty)
			Some(newCompany(ownerId, companyName))
		else
			companyOptions.find { _.name ~== companyName } match
			{
				case Some(exactMatch) => join(ownerId, exactMatch)
				case None =>
					selectFrom(companyOptions.map { c => c -> s"${c.name} (${c.yCode})" }.sortBy { _._2 },
						"join") match
					{
						case Some(selected) => join(ownerId, selected)
						case None =>
							if (StdIn.ask(s"Do you want to insert a new company '$companyName' instead?"))
								Some(newCompany(ownerId, companyName))
							else
								None
					}
			}
	}
	
	private def join(userId: Int, company: Company)(implicit connection: Connection): Option[Company] =
	{
		// Checks whether the user is already a member of that company
		val memberships = company.access.memberships
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
				val languageCode = StdIn.read(
					"What language the company name is in? (default = en)").stringOr("en")
				linkCompanyToNewOrganization(userId, company, languageCode)
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
						val organizationDescriptions =
							DbDescriptions.ofOrganizationsWithIds(organizationIds).pull
						// Selects one of them
						selectFrom(organizationIds.toVector
							.map { orgId => orgId -> organizationDescriptions.filter { _.targetId == orgId }
								.map { _.description }.sortBy { _.roleId }.map { _.text }.mkString(" / ") }
							.filter { _._2.nonEmpty }, "link")
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
	
	// Expects exact match to be tested at this point
	private def selectFrom[A](options: Seq[(A, String)], selectionVerb: String = "select"): Option[A] =
	{
		if (options.isEmpty)
		{
			println("No matches found")
			None
		}
		else
		{
			val bestNameMatches = options.map { _._2 }.sortBy { _.length }.take(3)
			println(s"Found ${options.size} matches: ${bestNameMatches.mkString(", ")}${
				if (options.size > bestNameMatches.size) "..." else ""}")
			if (StdIn.ask(s"Do you want to $selectionVerb one of those companies?"))
				Some(_selectFrom(options))
			else
				None
		}
	}
	// Options must be of size 1 or more
	private def _selectFrom[A](options: Seq[(A, String)]): A =
	{
		def _narrow(filter: String): A =
		{
			val narrowed = options.filter { _._2.toLowerCase.contains(filter) }
			if (narrowed.isEmpty)
			{
				println("No results could be found with that filter, please try again")
				_selectFrom(options)
			}
			else
				narrowed.find { _._2 ~== filter }.map { _._1 }.getOrElse { _selectFrom(narrowed) }
		}
		
		if (options.size == 1)
		{
			val (result, resultName) = options.head
			println(s"Found $resultName")
			result
		}
		else if (options.size > 10)
		{
			println(s"Found ${options.size} options")
			val filter = StdIn.readLineUntilNotEmpty(
				"Please narrow the selection by specifying an additional filter").toLowerCase
			_narrow(filter)
		}
		else
		{
			println(s"Found ${options.size} options")
			options.indices.foreach { index => println(s"${index + 1}: ${options(index)._2}") }
			println("Please select the correct index or narrow the selection by typing text")
			StdIn.readIterator.filter { _.isDefined }.findMap { input =>
				input.int match
				{
					case Some(index) =>
						if (index > 0 && index <= options.size)
							Some(Right(index))
						else
						{
							println("That index is out of range, please select a new one")
							None
						}
					case None => Some(Left(input.getString))
				}
			}.get match
			{
				case Right(index) => options(index - 1)._1
				case Left(filter) => _narrow(filter)
			}
		}
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
