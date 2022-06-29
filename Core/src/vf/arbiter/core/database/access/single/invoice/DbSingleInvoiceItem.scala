package vf.arbiter.core.database.access.single.invoice

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.model.stored.invoice.InvoiceItem

/**
  * An access point to individual invoice items, based on their id
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
case class DbSingleInvoiceItem(id: Int) 
	extends UniqueInvoiceItemAccess with SingleIntIdModelAccess[InvoiceItem]

