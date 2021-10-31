package vf.arbiter.command.database.access.single.device

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.command.model.stored.device.InvoiceForm

/**
  * An access point to individual InvoiceForms, based on their id
  * @since 2021-10-20
  */
case class DbSingleInvoiceForm(id: Int) 
	extends UniqueInvoiceFormAccess with SingleIntIdModelAccess[InvoiceForm]

