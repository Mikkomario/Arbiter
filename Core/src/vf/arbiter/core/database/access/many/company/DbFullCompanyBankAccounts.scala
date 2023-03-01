package vf.arbiter.core.database.access.many.company

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.NonDeprecatedView
import utopia.vault.sql.SqlExtensions._
import vf.arbiter.core.model.combined.company.FullCompanyBankAccount

/**
 * Used for reading company bank account information which includes bank information
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
object DbFullCompanyBankAccounts
	extends ManyFullCompanyBankAccountsAccess with NonDeprecatedView[FullCompanyBankAccount]
{
	// OTHER    ------------------------------
	
	/**
	 * @param ids Ids of targeted company bank accounts
	 * @return An access point to those bank accounts
	 */
	def apply(ids: Iterable[Int]) = new DbFullCompanyBankAccountsSubSet(ids)
	
	
	// NESTED   ------------------------------
	
	class DbFullCompanyBankAccountsSubSet(_ids: Iterable[Int]) extends ManyFullCompanyBankAccountsAccess
	{
		override def globalCondition = Some(index in _ids)
	}
}