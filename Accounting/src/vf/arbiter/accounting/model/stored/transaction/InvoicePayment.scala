package vf.arbiter.accounting.model.stored.transaction

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.accounting.database.access.single.transaction.invoice_payment.DbSingleInvoicePayment
import vf.arbiter.accounting.model.partial.transaction.InvoicePaymentData

/**
  * Represents a invoice payment that has already been stored in the database
  * @param id id of this invoice payment in the database
  * @param data Wrapped invoice payment data
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class InvoicePayment(id: Int, data: InvoicePaymentData) 
	extends StoredModelConvertible[InvoicePaymentData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this invoice payment in the database
	  */
	def access = DbSingleInvoicePayment(id)
}

