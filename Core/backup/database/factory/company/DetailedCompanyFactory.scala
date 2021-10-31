package vf.arbiter.core.database.factory.company

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.core.model.combined.company.DetailedCompany
import vf.arbiter.core.model.stored.company.{Company, CompanyDetails}

/**
  * Used for reading DetailedCompanys from the database
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DetailedCompanyFactory 
	extends CombiningFactory[DetailedCompany, Company, CompanyDetails] with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = CompanyDetailsFactory
	
	override def nonDeprecatedCondition = childFactory.nonDeprecatedCondition
	
	override def parentFactory = CompanyFactory
	
	override def apply(company: Company, details: CompanyDetails) = DetailedCompany(company, details)
}

