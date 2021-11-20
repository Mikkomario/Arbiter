package vf.arbiter.core.model.stored.invoice

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.invoice.DbSingleInvoice
import vf.arbiter.core.model.combined.invoice.InvoiceWithItems
import vf.arbiter.core.model.partial.invoice.InvoiceData

/**
  * Represents a Invoice that has already been stored in the database
  * @param id id of this Invoice in the database
  * @param data Wrapped Invoice data
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class Invoice(id: Int, data: InvoiceData) extends StoredModelConvertible[InvoiceData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this Invoice in the database
	  */
	def access = DbSingleInvoice(id)
	
	
	// OTHER    --------------------
	
	/**
	 * @param items Items of this invoice
	 * @return A copy of this invoice with those items
	 */
	def withItems(items: Vector[InvoiceItem]) = InvoiceWithItems(this, items)
}

