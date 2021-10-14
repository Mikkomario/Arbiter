package vf.arbiter.core.database.access.single.invoice

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * An access point to individual Invoices, based on their id
  * @since 2021-10-14
  */
case class DbSingleInvoice(id: Int) extends UniqueInvoiceAccess with SingleIntIdModelAccess[Invoice]

