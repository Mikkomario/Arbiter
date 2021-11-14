package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.model.combined.company.FullCompanyBankAccount

/**
 * Used for reading company bank account information which includes bank information
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
object DbFullCompanyBankAccounts
	extends ManyFullCompanyBankAccountsAccess with NonDeprecatedView[FullCompanyBankAccount]
