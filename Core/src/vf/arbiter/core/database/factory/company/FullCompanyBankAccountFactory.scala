package vf.arbiter.core.database.factory.company

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.core.model.combined.company.FullCompanyBankAccount
import vf.arbiter.core.model.stored.company.{Bank, CompanyBankAccount}

/**
  * Used for reading FullCompanyBankAccounts from the database
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object FullCompanyBankAccountFactory 
	extends CombiningFactory[FullCompanyBankAccount, CompanyBankAccount, Bank] with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = BankFactory
	
	override def nonDeprecatedCondition = parentFactory.nonDeprecatedCondition
	
	override def parentFactory = CompanyBankAccountFactory
	
	override def apply(account: CompanyBankAccount, bank: Bank) = FullCompanyBankAccount(account, bank)
}

