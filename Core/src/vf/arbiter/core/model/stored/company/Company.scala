package vf.arbiter.core.model.stored.company

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.company.DbCompany
import vf.arbiter.core.model.partial.company.CompanyData

/**
  * Represents a Company that has already been stored in the database
  * @param id id of this Company in the database
  * @param data Wrapped Company data
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class Company(id: Int, data: CompanyData) extends StoredModelConvertible[CompanyData]
{
	/**
	 * @return An access point to this company in the database
	 */
	def access = DbCompany(id)
}
