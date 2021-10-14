package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple OrganizationCompanies at a time
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object DbOrganizationCompanies extends ManyOrganizationCompaniesAccess with UnconditionalView
{
	// OTHER    ---------------------------------------
	
	/**
	 * @param companyId A company id
	 * @return An access point to links between that company and organizations
	 */
	def linkedToCompanyWithId(companyId: Int) = new DbCompanyOrganizationLinks(companyId)
	
	
	// NESTED   ---------------------------------------
	
	class DbCompanyOrganizationLinks(val companyId: Int) extends ManyOrganizationCompaniesAccess
	{
		// IMPLEMENTED  -------------------------------
		
		override def globalCondition = Some(model.withCompanyId(companyId).toCondition)
	}
}