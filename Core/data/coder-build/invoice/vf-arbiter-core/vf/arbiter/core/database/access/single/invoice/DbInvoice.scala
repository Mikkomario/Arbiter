package vf.arbiter.core.database.access.single.invoice

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.invoice.InvoiceFactory
import vf.arbiter.core.database.model.invoice.InvoiceModel
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * Used for accessing individual Invoices
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
object DbInvoice extends SingleRowModelAccess[Invoice] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoiceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted Invoice instance
	  * @return An access point to that Invoice
	  */
	def apply(id: Int) = new DbSingleInvoice(id)
	
	
	// NESTED	--------------------
	
	class DbSingleInvoice(val id: Int) extends UniqueInvoiceAccess with UniqueModelAccess[Invoice]
	{
		// IMPLEMENTED	--------------------
		
		override def condition = index <=> id
	}
}

