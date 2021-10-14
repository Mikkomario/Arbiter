package vf.arbiter.core.database.access.many.invoice

import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * The root access point when targeting multiple Invoices at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbInvoices extends ManyInvoicesAccess with NonDeprecatedView[Invoice]

