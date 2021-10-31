package vf.arbiter.core.model.stored.invoice

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.invoice.DbSingleInvoicePayment
import vf.arbiter.core.model.partial.invoice.InvoicePaymentData

/**
  * Represents a InvoicePayment that has already been stored in the database
  * @param id id of this InvoicePayment in the database
  * @param data Wrapped InvoicePayment data
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class InvoicePayment(id: Int, data: InvoicePaymentData) 
	extends StoredModelConvertible[InvoicePaymentData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this InvoicePayment in the database
	  */
	def access = DbSingleInvoicePayment(id)
}

