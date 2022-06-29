package vf.arbiter.core.model.combined.invoice

import utopia.flow.util.Extender
import vf.arbiter.core.model.partial.invoice.InvoiceData
import vf.arbiter.core.model.stored.invoice.{Invoice, InvoicePayment}

/**
  * Combines invoice with payment data
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
case class InvoiceWithPayment(invoice: Invoice, payment: Option[InvoicePayment]) extends Extender[InvoiceData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this invoice in the database
	  */
	def id = invoice.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = invoice.data
}

