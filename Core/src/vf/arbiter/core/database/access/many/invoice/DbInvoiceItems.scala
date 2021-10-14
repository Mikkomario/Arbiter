package vf.arbiter.core.database.access.many.invoice

import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple InvoiceItems at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbInvoiceItems extends ManyInvoiceItemsAccess with UnconditionalView

