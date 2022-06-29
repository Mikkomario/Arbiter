package vf.arbiter.core.database.access.single.invoice

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.database.factory.invoice.InvoiceFactory
import vf.arbiter.core.database.model.invoice.InvoiceModel
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * Used for accessing individual invoices
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
object DbInvoice extends SingleRowModelAccess[Invoice] with NonDeprecatedView[Invoice] with Indexed
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
	  * @param id Database id of the targeted invoice
	  * @return An access point to that invoice
	  */
	def apply(id: Int) = DbSingleInvoice(id)
	
	/**
	  * @param reference A reference code
	  * @param connection Implicit DB Connection
	  * @return Invoice with that reference code
	  */
	def withReferenceCode(reference: String)(implicit connection: Connection) = 
		find(model.withReferenceCode(reference).toCondition)
}

