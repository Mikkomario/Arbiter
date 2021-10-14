package vf.arbiter.core.database.access.single.company

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.model.stored.company.CompanyBankAccount

/**
  * An access point to individual CompanyBankAccounts, based on their id
  * @since 2021-10-14
  */
case class DbSingleCompanyBankAccount(id: Int) 
	extends UniqueCompanyBankAccountAccess with SingleIntIdModelAccess[CompanyBankAccount]

