package vf.arbiter.core.database.access.single.invoice

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.model.stored.invoice.InvoiceItem

/**
  * An access point to individual InvoiceItems, based on their id
  * @since 2021-10-14
  */
case class DbSingleInvoiceItem(id: Int) 
	extends UniqueInvoiceItemAccess with SingleIntIdModelAccess[InvoiceItem]

