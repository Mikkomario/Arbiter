package vf.arbiter.core.database.access.single.company

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.database.access.many.company.{DbCompanyBankAccounts, DbCompanyProducts, DbFullCompanyBankAccounts, DbOrganizationCompanies}
import vf.arbiter.core.database.model.company.OrganizationCompanyModel
import vf.arbiter.core.model.partial.company.OrganizationCompanyData
import vf.arbiter.core.model.stored.company.Company

/**
  * An access point to individual Companies, based on their id
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class DbSingleCompany(id: Int) extends UniqueCompanyAccess with SingleIntIdModelAccess[Company]
{
	// COMPUTED -------------------------------
	
	/**
	 * @return An access point to this company's products
	 */
	def products = DbCompanyProducts.ofCompanyWithId(id)
	/**
	 * @return An access point to this company's bank accounts
	 */
	def bankAccounts = DbCompanyBankAccounts.belongingToCompanyWithId(id)
	/**
	 * @return An access point to bank accounts belonging to this company (including bank information)
	 */
	@deprecated("Please use bankAccounts.full instead", "v1.2")
	def fullBankAccounts = DbFullCompanyBankAccounts.belongingToCompanyWithId(id)
	
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
	
	
	// METHODS  ----------------------------------
	
	/**
	 * Connects this company to an organization - Doesn't check for existing connections
	 * @param organizationId Id of the linked organization
	 * @param connection DB Connection (implicit)
	 * @return Link that was formed between these organizations
	 */
	def linkToOrganizationWithId(organizationId: Int)(implicit connection: Connection) =
		OrganizationCompanyModel.insert(OrganizationCompanyData(organizationId, id))
}