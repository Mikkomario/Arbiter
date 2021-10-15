package vf.arbiter.core.model.stored.company

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.company.DbSingleCompany
import vf.arbiter.core.model.combined.company.DetailedCompany
import vf.arbiter.core.model.partial.company.CompanyData

/**
  * Represents a Company that has already been stored in the database
  * @param id id of this Company in the database
  * @param data Wrapped Company data
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class Company(id: Int, data: CompanyData) extends StoredModelConvertible[CompanyData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this Company in the database
	  */
	def access = DbSingleCompany(id)
	
	
	// OTHER    --------------------
	
	/**
	 * @param details Details for this company
	 * @return A detailed version of this company
	 */
	def +(details: CompanyDetails) = DetailedCompany(this, details)
}

