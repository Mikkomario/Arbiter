package vf.arbiter.core.database.access.single.invoice

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.database.access.many.invoice.DbInvoiceItems
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * An access point to individual invoices, based on their id
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
case class DbSingleInvoice(id: Int) extends UniqueInvoiceAccess with SingleIntIdModelAccess[Invoice]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this invoice's items
	  */
	def items = DbInvoiceItems.forInvoiceWithId(id)
}

