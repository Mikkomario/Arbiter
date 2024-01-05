package vf.arbiter.accounting.database.access.single.transaction.invoice_payment

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.accounting.model.stored.transaction.InvoicePayment

/**
  * An access point to individual invoice payments, based on their id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class DbSingleInvoicePayment(id: Int) 
	extends UniqueInvoicePaymentAccess with SingleIntIdModelAccess[InvoicePayment]

