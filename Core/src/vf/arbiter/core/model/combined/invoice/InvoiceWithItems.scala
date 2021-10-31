package vf.arbiter.core.model.combined.invoice

import utopia.flow.util.Extender
import vf.arbiter.core.model.partial.invoice.InvoiceData
import vf.arbiter.core.model.stored.invoice.{Invoice, InvoiceItem}

/**
  * Combines Invoice with items data
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class InvoiceWithItems(invoice: Invoice, items: Vector[InvoiceItem]) extends Extender[InvoiceData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this Invoice in the database
	  */
	def id = invoice.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = invoice.data
}

