package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.company.CompanyBankAccountFactory
import vf.arbiter.core.model.stored.company.CompanyBankAccount

object ManyCompanyBankAccountsAccess
{
	// NESTED	--------------------
	
	private class ManyCompanyBankAccountsSubView(override val parent: ManyRowModelAccess[CompanyBankAccount], 
		override val filterCondition: Condition) 
		extends ManyCompanyBankAccountsAccess with SubView
}

/**
  * A common trait for access points which target multiple CompanyBankAccounts at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait ManyCompanyBankAccountsAccess
	extends ManyCompanyBankAccountsAccessLike[CompanyBankAccount, ManyCompanyBankAccountsAccess]
{
	// COMPUTED	--------------------
	
	/**
	 * Factory used for constructing database the interaction models
	 */
	protected def model = accountModel
	
	/**
	 * @return An access point to "full" versions of these accounts (include bank information)
	 */
	def full = globalCondition match
	{
		case Some(condition) => DbFullCompanyBankAccounts.filter(condition)
		case None => DbFullCompanyBankAccounts
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override def factory = CompanyBankAccountFactory
	
	override def _filter(additionalCondition: Condition): ManyCompanyBankAccountsAccess =
		new ManyCompanyBankAccountsAccess.ManyCompanyBankAccountsSubView(this, additionalCondition)
}

