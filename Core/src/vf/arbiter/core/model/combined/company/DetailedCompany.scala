package vf.arbiter.core.model.combined.company

import utopia.flow.util.Extender
import vf.arbiter.core.model.partial.company.CompanyData
import vf.arbiter.core.model.stored.company.{Company, CompanyDetails}

/**
  * Combines Company with details data
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class DetailedCompany(company: Company, details: CompanyDetails) extends Extender[CompanyData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this Company in the database
	  */
	def id = company.id
	
	/**
	 * @return Name and Y-code of this company
	 */
	def nameAndYCode = s"${details.name} (${company.yCode})"
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = company.data
}

