package vf.arbiter.core.model.combined.company

import utopia.flow.generic.model.immutable.Constant
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.view.template.Extender
import vf.arbiter.core.model.combined.location.FullStreetAddress
import vf.arbiter.core.model.partial.company.CompanyDetailsData
import vf.arbiter.core.model.stored.company.CompanyDetails
import vf.arbiter.core.model.template.Exportable

/**
 * Adds full address information to company details
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
case class FullCompanyDetails(details: CompanyDetails, address: FullStreetAddress)
	extends Extender[CompanyDetailsData] with Exportable
{
	// COMPUTED ----------------------------
	
	/**
	 * @return Company details id
	 */
	def id = details.id
	
	
	// IMPLEMENTED  ------------------------
	
	override def wrapped = details.data
	
	override def toExportModel =
		(Constant("id", details.id) +: details.toExportModel) + Constant("address", address.toExportModel)
}
