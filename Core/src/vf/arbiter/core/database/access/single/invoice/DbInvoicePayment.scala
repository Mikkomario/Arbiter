package vf.arbiter.core.database.access.single.invoice

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.invoice.InvoicePaymentFactory
import vf.arbiter.core.database.model.invoice.InvoicePaymentModel
import vf.arbiter.core.model.stored.invoice.InvoicePayment

/**
  * Used for accessing individual invoice payments
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
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
}

