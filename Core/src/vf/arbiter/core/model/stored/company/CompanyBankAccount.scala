package vf.arbiter.core.model.stored.company

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.company.DbSingleCompanyBankAccount
import vf.arbiter.core.model.partial.company.CompanyBankAccountData

/**
  * Represents a CompanyBankAccount that has already been stored in the database
  * @param id id of this CompanyBankAccount in the database
  * @param data Wrapped CompanyBankAccount data
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class CompanyBankAccount(id: Int, data: CompanyBankAccountData) 
	extends StoredModelConvertible[CompanyBankAccountData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this CompanyBankAccount in the database
	  */
	def access = DbSingleCompanyBankAccount(id)
}

