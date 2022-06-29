package vf.arbiter.core.model.combined.invoice

import utopia.flow.util.Extender
import vf.arbiter.core.model.partial.invoice.InvoiceData
import vf.arbiter.core.model.stored.invoice.{Invoice, InvoiceItem}

/**
  * Combines invoice with items data
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
case class InvoiceWithItems(invoice: Invoice, items: Vector[InvoiceItem]) extends Extender[InvoiceData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this invoice in the database
	  */
	def id = invoice.id
	
	/**
	  * Total price of this invoice, without any taxes included
	  */
	def price = items.map { _.price }.sum
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = invoice.data
}

