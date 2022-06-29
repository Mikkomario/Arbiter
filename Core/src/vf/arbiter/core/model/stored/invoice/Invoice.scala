package vf.arbiter.core.model.stored.invoice

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.invoice.DbSingleInvoice
import vf.arbiter.core.model.combined.invoice.InvoiceWithItems
import vf.arbiter.core.model.partial.invoice.InvoiceData

/**
  * Represents a invoice that has already been stored in the database
  * @param id id of this invoice in the database
  * @param data Wrapped invoice data
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
case class Invoice(id: Int, data: InvoiceData) extends StoredModelConvertible[InvoiceData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this invoice in the database
	  */
	def access = DbSingleInvoice(id)
	
	
	// OTHER	--------------------
	
	/**
	  * @param items Items of this invoice
	  * @return A copy of this invoice with those items
	  */
	def withItems(items: Vector[InvoiceItem]) = InvoiceWithItems(this, items)
}

