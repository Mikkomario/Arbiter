package vf.arbiter.core.model.stored.company

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.model.partial.company.CompanyBankAddressData

/**
  * Represents a CompanyBankAddress that has already been stored in the database
  * @param id id of this CompanyBankAddress in the database
  * @param data Wrapped CompanyBankAddress data
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class CompanyBankAddress(id: Int, data: CompanyBankAddressData) 
	extends StoredModelConvertible[CompanyBankAddressData]

