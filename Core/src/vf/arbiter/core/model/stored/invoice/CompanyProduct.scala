package vf.arbiter.core.model.stored.invoice

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.model.partial.invoice.CompanyProductData

/**
  * Represents a CompanyProduct that has already been stored in the database
  * @param id id of this CompanyProduct in the database
  * @param data Wrapped CompanyProduct data
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
case class CompanyProduct(id: Int, data: CompanyProductData) 
	extends StoredModelConvertible[CompanyProductData]

