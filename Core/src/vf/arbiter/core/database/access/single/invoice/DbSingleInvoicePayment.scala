package vf.arbiter.core.database.access.single.invoice

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.model.stored.invoice.InvoicePayment

/**
  * An access point to individual invoice payments, based on their id
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
case class DbSingleInvoicePayment(id: Int) 
	extends UniqueInvoicePaymentAccess with SingleIntIdModelAccess[InvoicePayment]

