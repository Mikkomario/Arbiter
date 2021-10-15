package vf.arbiter.core.database.access.many.company

import utopia.vault.database.Connection
import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.database.factory.company.DetailedCompanyFactory
import vf.arbiter.core.database.model.company.CompanyDetailsModel
import vf.arbiter.core.model.combined.company.DetailedCompany

/**
 * Used for accessing multiple detailed companies at a time
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
object DbDetailedCompanies extends ManyCompaniesAccessLike[DetailedCompany] with NonDeprecatedView[DetailedCompany]
{
	// COMPUTED --------------------------------------
	
	private def detailsModel = CompanyDetailsModel
	
	
	// IMPLEMENTED  ----------------------------------
	
	override def factory = DetailedCompanyFactory
	
	override protected def defaultOrdering = None
	
	
	// OTHER    --------------------------------------
	
	/**
	 * Finds companies within this group that contain the specified string in their name
	 * @param companyNamePart String that must be contained within a company name
	 * @param connection Implicit DB Connection
	 * @return Companies that have the specified string in their name
	 */
	def matchingName(companyNamePart: String)(implicit connection: Connection) =
		find(detailsModel.nameMatchCondition(companyNamePart))
}
