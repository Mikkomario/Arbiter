package vf.arbiter.accounting.database.access.many.transaction.invoice_payment

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.InvoicePaymentFactory
import vf.arbiter.accounting.model.stored.transaction.InvoicePayment

object ManyInvoicePaymentsAccess
{
	// NESTED	--------------------
	
	private class ManyInvoicePaymentsSubView(condition: Condition) extends ManyInvoicePaymentsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
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
	
	override def filter(filterCondition: Condition): ManyInvoicePaymentsAccess = 
		new ManyInvoicePaymentsAccess.ManyInvoicePaymentsSubView(mergeCondition(filterCondition))
}

