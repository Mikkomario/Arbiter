package vf.arbiter.core.database.access.many.company

import utopia.vault.database.Connection
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.company.DetailedCompanyFactory
import vf.arbiter.core.database.model.company.CompanyDetailsModel
import vf.arbiter.core.model.combined.company.DetailedCompany

object ManyDetailedCompaniesAccess extends ViewFactory[ManyDetailedCompaniesAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyDetailedCompaniesAccess = 
		_ManyDetailedCompaniesAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyDetailedCompaniesAccess(override val accessCondition: Option[Condition]) 
		extends ManyDetailedCompaniesAccess
}

/**
  * A common trait for access points which yield multiple detailed companies at a time
  * @author Mikko Hilpinen
  * @since 27.12.2021, v1.2
  */
trait ManyDetailedCompaniesAccess 
	extends ManyCompaniesAccessLike[DetailedCompany, ManyDetailedCompaniesAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * Model used for interacting with company details
	  */
	protected def detailsModel = CompanyDetailsModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DetailedCompanyFactory
	
	override def self = this
	
	
	// OTHER	--------------------
	
	def apply(condition: Condition): ManyDetailedCompaniesAccess = ManyDetailedCompaniesAccess(condition)
	
	/**
	  * Finds companies within this group that contain the specified string in their name
	  * @param companyNamePart String that must be contained within a company name
	  * @param connection Implicit DB Connection
	  * @return Companies that have the specified string in their name
	  */
	def matchingName(companyNamePart: String)(implicit connection: Connection) = 
		find(detailsModel.nameMatchCondition(companyNamePart))
}

