package vf.arbiter.core.model.combined.company

import utopia.flow.datastructure.immutable.Constant
import utopia.flow.generic.ValueConversions._
import utopia.flow.util.Extender
import vf.arbiter.core.model.partial.company.CompanyBankAccountData
import vf.arbiter.core.model.stored.company.{Bank, CompanyBankAccount}
import vf.arbiter.core.model.template.Exportable

/**
  * Combines account with Bank data
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class FullCompanyBankAccount(account: CompanyBankAccount, bank: Bank) 
	extends Extender[CompanyBankAccountData] with Exportable
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this account in the database
	  */
	def id = account.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = account.data
	
	override def toExportModel = Constant("bic", bank.bic) +: account.toExportModel
}

