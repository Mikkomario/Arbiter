package vf.arbiter.core.database.access.single.invoice

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.invoice.InvoicePaymentFactory
import vf.arbiter.core.database.model.invoice.InvoicePaymentModel
import vf.arbiter.core.model.stored.invoice.InvoicePayment

/**
  * Used for accessing individual InvoicePayments
  * @author Mikko Hilpinen
  * @since 2021-10-31
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
	  * @param id Database id of the targeted InvoicePayment instance
	  * @return An access point to that InvoicePayment
	  */
	def apply(id: Int) = DbSingleInvoicePayment(id)
}

