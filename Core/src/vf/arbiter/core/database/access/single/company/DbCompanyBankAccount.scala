package vf.arbiter.core.database.access.single.company

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.database.factory.company.CompanyBankAccountFactory
import vf.arbiter.core.database.model.company.CompanyBankAccountModel
import vf.arbiter.core.model.stored.company.CompanyBankAccount

/**
  * Used for accessing individual CompanyBankAccounts
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbCompanyBankAccount 
	extends SingleRowModelAccess[CompanyBankAccount] with NonDeprecatedView[CompanyBankAccount] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyBankAccountModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyBankAccountFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted CompanyBankAccount instance
	  * @return An access point to that CompanyBankAccount
	  */
	def apply(id: Int) = DbSingleCompanyBankAccount(id)
}

