package vf.arbiter.core.database.access.single.invoice

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.model.stored.invoice.InvoicePayment

/**
  * An access point to individual InvoicePayments, based on their id
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class DbSingleInvoicePayment(id: Int) 
	extends UniqueInvoicePaymentAccess with SingleIntIdModelAccess[InvoicePayment]

