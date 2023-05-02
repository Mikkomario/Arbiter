package vf.arbiter.core.database.access.many.company

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import vf.arbiter.core.database.access.many.company.ManyDetailedCompaniesAccess.SubAccess
import vf.arbiter.core.database.factory.company.DetailedCompanyFactory
import vf.arbiter.core.database.model.company.CompanyDetailsModel
import vf.arbiter.core.model.combined.company.DetailedCompany

object ManyDetailedCompaniesAccess
{
	private class SubAccess(override val parent: ManyModelAccess[DetailedCompany],
	                        override val filterCondition: Condition) extends ManyDetailedCompaniesAccess with SubView
}

/**
 * A common trait for access points which yield multiple detailed companies at a time
 * @author Mikko Hilpinen
 * @since 27.12.2021, v1.2
 */
trait ManyDetailedCompaniesAccess extends ManyCompaniesAccessLike[DetailedCompany, ManyDetailedCompaniesAccess]
{
	// COMPUTED --------------------------------------
	
	/**
	 * @return Model used for interacting with company details
	 */
	protected def detailsModel = CompanyDetailsModel
	
	
	// IMPLEMENTED  --------------------------
	
	override def self = this
	
	override def factory = DetailedCompanyFactory
	
	override def filter(additionalCondition: Condition): ManyDetailedCompaniesAccess =
		new SubAccess(this, additionalCondition)
	
	
	// OTHER    --------------------------------------
	
	/**
	 * Finds companies within this group that contain the specified string in their name
	 * @param companyNamePart String that must be contained within a company name
	 * @param connection Implicit DB Connection
	 * @return Companies that have the specified string in their name
	 */
	 // TODO: Use filter instead of find
	def matchingName(companyNamePart: String)(implicit connection: Connection) =
		find(detailsModel.nameMatchCondition(companyNamePart))
}
