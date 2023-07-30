package vf.arbiter.core.model.combined.company

import utopia.flow.view.template.Extender
import vf.arbiter.core.model.partial.company.CompanyData
import vf.arbiter.core.model.stored.company.Company

/**
 * Combines company information with inclusive details
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
case class FullyDetailedCompany(company: Company, details: FullCompanyDetails) extends Extender[CompanyData]
{
	// COMPUTED -----------------------
	
	/**
	 * @return Id of this company
	 */
	def id = company.id
	
	/**
	 * @return Copy of this company without all details included
	 */
	def toDetailedCompany = company + details.details
	
	
	// IMPLEMENTED  -------------------
	
	override def wrapped = company.data
}
