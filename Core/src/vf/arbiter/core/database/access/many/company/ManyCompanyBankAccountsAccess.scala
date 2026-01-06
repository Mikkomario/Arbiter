package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.company.CompanyBankAccountFactory
import vf.arbiter.core.model.stored.company.CompanyBankAccount

object ManyCompanyBankAccountsAccess extends ViewFactory[ManyCompanyBankAccountsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyCompanyBankAccountsAccess = 
		_ManyCompanyBankAccountsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyCompanyBankAccountsAccess(override val accessCondition: Option[Condition]) 
		extends ManyCompanyBankAccountsAccess
}

/**
  * A common trait for access points which target multiple CompanyBankAccounts at a time
  * @author Mikko Hilpinen
  * @since 31.10.2021
  */
trait ManyCompanyBankAccountsAccess 
	extends ManyCompanyBankAccountsAccessLike[CompanyBankAccount, ManyCompanyBankAccountsAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to "full" versions of these accounts (include bank information)
	  */
	def full = {
		accessCondition match
		{
			case Some(condition) => DbFullCompanyBankAccounts.filter(condition)
			case None => DbFullCompanyBankAccounts
		}
	}
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	def model = accountModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyBankAccountFactory
	
	override def self = this
	
	override def apply(condition: Condition): ManyCompanyBankAccountsAccess = 
		ManyCompanyBankAccountsAccess(condition)
}

