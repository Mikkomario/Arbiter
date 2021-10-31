package vf.arbiter.core.database.factory.invoice

import utopia.vault.nosql.factory.row.linked.PossiblyCombiningFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.core.model.combined.invoice.InvoiceWithPayment
import vf.arbiter.core.model.stored.invoice.{Invoice, InvoicePayment}

/**
  * Used for reading InvoiceWithPayments from the database
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object InvoiceWithPaymentFactory 
	extends PossiblyCombiningFactory[InvoiceWithPayment, Invoice, InvoicePayment] with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = InvoicePaymentFactory
	
	override def nonDeprecatedCondition = parentFactory.nonDeprecatedCondition
	
	override def parentFactory = InvoiceFactory
	
	override def apply(invoice: Invoice, payment: Option[InvoicePayment]) = InvoiceWithPayment(invoice, 
		payment)
}

