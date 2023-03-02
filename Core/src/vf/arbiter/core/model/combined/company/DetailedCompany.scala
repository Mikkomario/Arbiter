package vf.arbiter.core.model.combined.company

import utopia.flow.view.template.Extender
import utopia.vault.database.Connection
import vf.arbiter.core.model.combined.location.FullStreetAddress
import vf.arbiter.core.model.partial.company.CompanyData
import vf.arbiter.core.model.stored.company.{Company, CompanyDetails}

/**
  * Combines Company with details data
  * @author Mikko Hilpinen
  * @since 2021-10-31
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
	
	/**
	 * @param connection Implicit DB connection
	 * @return A copy of these details where address information has been pulled from the database
	 */
	def pullFull(implicit connection: Connection) =
		this + details.addressAccess.full
			.getOrElse { throw new IllegalStateException(s"No address data for id ${details.addressId}") }
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = company.data
	
	
	// OTHER    ------------------------
	
	/**
	 * @param address Street address data (inclusive)
	 * @return This company information with that address data included
	 */
	def +(address: FullStreetAddress) = FullyDetailedCompany(company, details + address)
}

