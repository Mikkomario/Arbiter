package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import vf.arbiter.core.database.access.many.company.ManyFullCompanyBankAccountsAccess.SubAccess
import vf.arbiter.core.database.factory.company.FullCompanyBankAccountFactory
import vf.arbiter.core.model.combined.company.FullCompanyBankAccount

object ManyFullCompanyBankAccountsAccess
{
	private class SubAccess(override val parent: ManyModelAccess[FullCompanyBankAccount],
	                        override val filterCondition: Condition)
		extends ManyFullCompanyBankAccountsAccess with SubView
}

/**
 * Used for accessing multiple company bank accounts at a time, including bank information
 * @author Mikko Hilpinen
 * @since 14.11.2021, v1.2
 */
trait ManyFullCompanyBankAccountsAccess
	extends ManyCompanyBankAccountsAccessLike[FullCompanyBankAccount, ManyFullCompanyBankAccountsAccess]
{
	override def self = this
	
	override def factory = FullCompanyBankAccountFactory
	
	override protected def _filter(condition: Condition): ManyFullCompanyBankAccountsAccess =
		new SubAccess(this, condition)
}
