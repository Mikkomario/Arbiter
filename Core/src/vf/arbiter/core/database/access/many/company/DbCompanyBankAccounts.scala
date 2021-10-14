package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.model.stored.company.CompanyBankAccount

/**
  * The root access point when targeting multiple CompanyBankAccounts at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbCompanyBankAccounts extends ManyCompanyBankAccountsAccess with NonDeprecatedView[CompanyBankAccount]

