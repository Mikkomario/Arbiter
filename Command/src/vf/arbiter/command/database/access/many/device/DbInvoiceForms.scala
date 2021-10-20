package vf.arbiter.command.database.access.many.device

import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple InvoiceForms at a time
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
object DbInvoiceForms extends ManyInvoiceFormsAccess with UnconditionalView

