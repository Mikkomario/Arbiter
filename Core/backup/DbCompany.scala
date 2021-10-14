package vf.arbiter.core.database.access.single.company

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.access.many.company.{DbCompanies, DbOrganizationCompanies}
import vf.arbiter.core.database.access.many.invoice.DbCompanyProducts
import vf.arbiter.core.database.factory.company.CompanyFactory
import vf.arbiter.core.database.model.company.{CompanyModel, OrganizationCompanyModel}
import vf.arbiter.core.model.partial.company.OrganizationCompanyData
import vf.arbiter.core.model.stored.company.Company

/**
  * Used for accessing individual Companies
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object DbCompany extends SingleRowModelAccess[Company] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted Company instance
	  * @return An access point to that Company
	  */
	def apply(id: Int) = new DbSingleCompany(id)
	
	/**
	 * Finds a company with the specified name (exact, but case-insensitive)
	 * @param companyName A company name
	 * @param connection Implicit DB Connection
	 * @return Company with that name, if found
	 */
	def withName(companyName: String)(implicit connection: Connection) =
		find(model.withName(companyName).toCondition)
	
	/**
	 * Finds a company that contains the specified string in its name
	 * @param companyNamePart A string searched from company name
	 * @param connection Implicit DB Connection
	 * @return A company containing that string in its name. If multiple results are found, the shortest company
	 *         name is selected
	 */
	def matchingName(companyNamePart: String)(implicit connection: Connection) =
		DbCompanies.matchingName(companyNamePart).minByOption { _.name.length }
	
	
	// NESTED	--------------------
	
	class DbSingleCompany(val id: Int) extends UniqueCompanyAccess with UniqueModelAccess[Company]
	{
		// COMPUTED ------------------------
		
		/**
		 * @return An access point to this company's products
		 */
		def products = DbCompanyProducts.ofCompanyWithId(id)
		
		/**
		 * @param connection Implicit DB Connection
		 * @return Organization ids linked with this company
		 */
		def linkedOrganizationIds(implicit connection: Connection) =
			DbOrganizationCompanies.linkedToCompanyWithId(id).organizationIds
		
		/**
		 * @param connection Implicit DB Connection
		 * @return Memberships of organizations linked with this company
		 */
		def memberships(implicit connection: Connection) =
			DbOrganizationCompanies.linkedToCompanyWithId(id).memberships
		
		
		// IMPLEMENTED	--------------------
		
		override def condition = index <=> id
		
		
		// OTHER    ------------------------
		
		/**
		 * Connects this company to an organization - Doesn't check for existing connections
		 * @param organizationId Id of the linked organization
		 * @param connection DB Connection (implicit)
		 * @return Link that was formed between these organizations
		 */
		def linkToOrganizationWithId(organizationId: Int)(implicit connection: Connection) =
			OrganizationCompanyModel.insert(OrganizationCompanyData(organizationId, id))
	}
}

