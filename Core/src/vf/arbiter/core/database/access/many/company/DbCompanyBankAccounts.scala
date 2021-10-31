package vf.arbiter.core.database.access.many.company

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.view.NonDeprecatedView
import utopia.vault.sql.SqlExtensions._
import vf.arbiter.core.model.stored.company.CompanyBankAccount

/**
  * The root access point when targeting multiple CompanyBankAccounts at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbCompanyBankAccounts extends ManyCompanyBankAccountsAccess with NonDeprecatedView[CompanyBankAccount]
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted CompanyBankAccounts
	  * @return An access point to CompanyBankAccounts with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbCompanyBankAccountsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbCompanyBankAccountsSubset(targetIds: Set[Int]) extends ManyCompanyBankAccountsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

