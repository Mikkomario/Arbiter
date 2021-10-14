package vf.arbiter.core.model.combined.company

import utopia.flow.util.Extender
import vf.arbiter.core.model.partial.company.CompanyBankAccountData
import vf.arbiter.core.model.stored.company.{Bank, CompanyBankAccount}

/**
  * Combines account with Bank data
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class FullCompanyBankAccount(account: CompanyBankAccount, bank: Bank) 
	extends Extender[CompanyBankAccountData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this account in the database
	  */
	def id = account.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = account.data
}

