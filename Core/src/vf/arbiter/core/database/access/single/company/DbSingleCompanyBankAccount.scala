package vf.arbiter.core.database.access.single.company

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.database.factory.company.FullCompanyBankAccountFactory
import vf.arbiter.core.model.stored.company.CompanyBankAccount

/**
  * An access point to individual CompanyBankAccounts, based on their id
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class DbSingleCompanyBankAccount(id: Int) 
	extends UniqueCompanyBankAccountAccess with SingleIntIdModelAccess[CompanyBankAccount]
{
	/**
	 * @param connection Implicit DB Connection
	 * @return A full copy of this bank account
	 */
	def full(implicit connection: Connection) =
		FullCompanyBankAccountFactory.get(condition)
}