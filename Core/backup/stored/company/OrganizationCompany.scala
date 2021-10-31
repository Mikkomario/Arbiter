package vf.arbiter.core.model.stored.company

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.company.DbSingleOrganizationCompany
import vf.arbiter.core.model.partial.company.OrganizationCompanyData

/**
  * Represents a OrganizationCompany that has already been stored in the database
  * @param id id of this OrganizationCompany in the database
  * @param data Wrapped OrganizationCompany data
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class OrganizationCompany(id: Int, data: OrganizationCompanyData) 
	extends StoredModelConvertible[OrganizationCompanyData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this OrganizationCompany in the database
	  */
	def access = DbSingleOrganizationCompany(id)
}

