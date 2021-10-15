package vf.arbiter.core.model.combined.company

import utopia.flow.util.Extender
import vf.arbiter.core.model.combined.location.FullStreetAddress
import vf.arbiter.core.model.partial.company.CompanyDetailsData
import vf.arbiter.core.model.stored.company.CompanyDetails

/**
 * Adds full address information to company details
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
case class FullCompanyDetails(details: CompanyDetails, address: FullStreetAddress) extends Extender[CompanyDetailsData]
{
	// COMPUTED ----------------------------
	
	/**
	 * @return Company details id
	 */
	def id = details.id
	
	
	// IMPLEMENTED  ------------------------
	
	override def wrapped = details.data
}
