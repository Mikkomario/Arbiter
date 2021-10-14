package vf.arbiter.core.model.stored.company

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.company.DbSingleCompanyProduct
import vf.arbiter.core.model.partial.company.CompanyProductData

/**
  * Represents a CompanyProduct that has already been stored in the database
  * @param id id of this CompanyProduct in the database
  * @param data Wrapped CompanyProduct data
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class CompanyProduct(id: Int, data: CompanyProductData) 
	extends StoredModelConvertible[CompanyProductData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this CompanyProduct in the database
	  */
	def access = DbSingleCompanyProduct(id)
}

