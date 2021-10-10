package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple CompanyBankAddresses at a time
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object DbCompanyBankAddresses extends ManyCompanyBankAddressesAccess with UnconditionalView

