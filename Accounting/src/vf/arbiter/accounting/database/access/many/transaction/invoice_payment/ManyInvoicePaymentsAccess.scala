package vf.arbiter.accounting.database.access.many.transaction.invoice_payment

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.InvoicePaymentFactory
import vf.arbiter.accounting.model.stored.transaction.InvoicePayment

object ManyInvoicePaymentsAccess extends ViewFactory[ManyInvoicePaymentsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyInvoicePaymentsAccess = 
		_ManyInvoicePaymentsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyInvoicePaymentsAccess(override val accessCondition: Option[Condition]) 
		extends ManyInvoicePaymentsAccess
}

/**
  * A common trait for access points which target multiple invoice payments at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait ManyInvoicePaymentsAccess 
	extends ManyInvoicePaymentsAccessLike[InvoicePayment, ManyInvoicePaymentsAccess] 
		with ManyRowModelAccess[InvoicePayment]
{
	// IMPLEMENTED	--------------------
	
	override def factory = InvoicePaymentFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyInvoicePaymentsAccess = ManyInvoicePaymentsAccess(condition)
}

