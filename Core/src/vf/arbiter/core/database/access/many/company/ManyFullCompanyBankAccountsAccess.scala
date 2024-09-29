package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.company.FullCompanyBankAccountFactory
import vf.arbiter.core.model.combined.company.FullCompanyBankAccount

object ManyFullCompanyBankAccountsAccess extends ViewFactory[ManyFullCompanyBankAccountsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyFullCompanyBankAccountsAccess = 
		_ManyFullCompanyBankAccountsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyFullCompanyBankAccountsAccess(override val accessCondition: Option[Condition]) 
		extends ManyFullCompanyBankAccountsAccess
}

/**
  * Used for accessing multiple company bank accounts at a time, including bank information
  * @author Mikko Hilpinen
  * @since 14.11.2021, v1.2
  */
trait ManyFullCompanyBankAccountsAccess 
	extends ManyCompanyBankAccountsAccessLike[FullCompanyBankAccount, ManyFullCompanyBankAccountsAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = FullCompanyBankAccountFactory
	
	override def self = this
	
	override def apply(condition: Condition): ManyFullCompanyBankAccountsAccess = 
		ManyFullCompanyBankAccountsAccess(condition)
}

