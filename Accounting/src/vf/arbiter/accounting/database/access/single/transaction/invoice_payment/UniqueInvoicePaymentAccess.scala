package vf.arbiter.accounting.database.access.single.transaction.invoice_payment

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.InvoicePaymentFactory
import vf.arbiter.accounting.model.stored.transaction.InvoicePayment

object UniqueInvoicePaymentAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueInvoicePaymentAccess = new _UniqueInvoicePaymentAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueInvoicePaymentAccess(condition: Condition) extends UniqueInvoicePaymentAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct invoice payments.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait UniqueInvoicePaymentAccess 
	extends UniqueInvoicePaymentAccessLike[InvoicePayment] with SingleRowModelAccess[InvoicePayment] 
		with FilterableView[UniqueInvoicePaymentAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = InvoicePaymentFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueInvoicePaymentAccess = 
		new UniqueInvoicePaymentAccess._UniqueInvoicePaymentAccess(mergeCondition(filterCondition))
}

