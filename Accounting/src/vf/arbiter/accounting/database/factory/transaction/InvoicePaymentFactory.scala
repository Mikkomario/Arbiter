package vf.arbiter.accounting.database.factory.transaction

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.accounting.database.ArbiterAccountingTables
import vf.arbiter.accounting.model.partial.transaction.InvoicePaymentData
import vf.arbiter.accounting.model.stored.transaction.InvoicePayment

/**
  * Used for reading invoice payment data from the DB
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object InvoicePaymentFactory extends FromValidatedRowModelFactory[InvoicePayment]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = ArbiterAccountingTables.invoicePayment
	
	override protected def fromValidatedModel(valid: Model) = 
		InvoicePayment(valid("id").getInt, InvoicePaymentData(valid("invoiceId").getInt, 
			valid("transactionId").getInt, valid("creatorId").int, valid("created").getInstant, 
			valid("manual").getBoolean))
}

