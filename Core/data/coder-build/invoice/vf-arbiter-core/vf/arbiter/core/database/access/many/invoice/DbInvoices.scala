package vf.arbiter.core.database.access.many.invoice

import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple Invoices at a time
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
object DbInvoices extends ManyInvoicesAccess with UnconditionalView

