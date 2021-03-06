package vf.arbiter.core.model.stored.company

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.company.DbSingleCompanyDetails
import vf.arbiter.core.model.combined.company.FullCompanyDetails
import vf.arbiter.core.model.combined.location.FullStreetAddress
import vf.arbiter.core.model.partial.company.CompanyDetailsData

/**
  * Represents a CompanyDetails that has already been stored in the database
  * @param id id of this CompanyDetails in the database
  * @param data Wrapped CompanyDetails data
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class CompanyDetails(id: Int, data: CompanyDetailsData) 
	extends StoredModelConvertible[CompanyDetailsData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this CompanyDetails in the database
	  */
	def access = DbSingleCompanyDetails(id)
	
	
	// OTHER    --------------------
	
	/**
	 * @param address Address information (inclusive)
	 * @return These details with that address information
	 */
	def +(address: FullStreetAddress) = FullCompanyDetails(this, address)
}

