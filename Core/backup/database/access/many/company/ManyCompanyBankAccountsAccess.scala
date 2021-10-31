package vf.arbiter.core.database.access.many.company

import utopia.vault.database.Connection
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
  * @since 2021-10-14
  */
trait ManyCompanyBankAccountsAccess extends ManyCompanyBankAccountsAccessLike[CompanyBankAccount]
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = accountModel
	
	/**
	 * @param connection Implicit DB Connection
	 * @return "full" versions of these accounts (include bank information)
	 */
	def full(implicit connection: Connection) = globalCondition match
	{
		case Some(condition) => DbFullCompanyBankAccounts.find(condition)
		case None => DbFullCompanyBankAccounts.all
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyBankAccountFactory
	
	override protected def defaultOrdering = None
	
	override def filter(additionalCondition: Condition): ManyCompanyBankAccountsAccess = 
		new ManyCompanyBankAccountsAccess.ManyCompanyBankAccountsSubView(this, additionalCondition)
}

