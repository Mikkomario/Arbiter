package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.database.factory.company.FullCompanyBankAccountFactory
import vf.arbiter.core.model.combined.company.FullCompanyBankAccount

/**
 * Used for reading company bank account information which includes bank information
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
object DbFullCompanyBankAccounts
	extends ManyCompanyBankAccountsAccessLike[FullCompanyBankAccount] with NonDeprecatedView[FullCompanyBankAccount]
{
	// IMPLEMENTED  ------------------------------------
	
	override protected def defaultOrdering = None
	
	override def factory = FullCompanyBankAccountFactory
}
