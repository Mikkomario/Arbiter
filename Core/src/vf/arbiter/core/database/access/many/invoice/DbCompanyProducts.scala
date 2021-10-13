package vf.arbiter.core.database.access.many.invoice

import utopia.vault.nosql.view.{NonDeprecatedView, SubView}
import vf.arbiter.core.model.stored.invoice.CompanyProduct

/**
  * The root access point when targeting multiple CompanyProducts at a time
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
object DbCompanyProducts extends ManyCompanyProductsAccess with NonDeprecatedView[CompanyProduct]
{
	// OTHER    --------------------------------
	
	/**
	 * @param companyId A company id
	 * @return An access point to that company's products
	 */
	def ofCompanyWithId(companyId: Int) = new DbProductsOfCompany(companyId)
	
	
	// NESTED   --------------------------------
	
	class DbProductsOfCompany(val companyId: Int) extends ManyCompanyProductsAccess with SubView
	{
		// IMPLEMENTED  ------------------------
		
		override protected def parent = DbCompanyProducts
		
		override def filterCondition = model.withCompanyId(companyId).toCondition
	}
}
