package vf.arbiter.core.database.access.single.invoice

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.database.access.many.invoice.DbInvoiceItems
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * An access point to individual Invoices, based on their id
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class DbSingleInvoice(id: Int) extends UniqueInvoiceAccess with SingleIntIdModelAccess[Invoice]
{
	/**
	 * @return An access point to this invoice's items
	 */
	def items = DbInvoiceItems.forInvoiceWithId(id)
}
