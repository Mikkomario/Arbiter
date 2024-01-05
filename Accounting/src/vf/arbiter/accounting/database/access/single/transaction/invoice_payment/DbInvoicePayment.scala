package vf.arbiter.accounting.database.access.single.transaction.invoice_payment

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.InvoicePaymentFactory
import vf.arbiter.accounting.database.model.transaction.InvoicePaymentModel
import vf.arbiter.accounting.model.stored.transaction.InvoicePayment

/**
  * Used for accessing individual invoice payments
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbInvoicePayment extends SingleRowModelAccess[InvoicePayment] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoicePaymentModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoicePaymentFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted invoice payment
	  * @return An access point to that invoice payment
	  */
	def apply(id: Int) = DbSingleInvoicePayment(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique invoice payments.
	  * @return An access point to the invoice payment that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueInvoicePaymentAccess(mergeCondition(condition))
}

