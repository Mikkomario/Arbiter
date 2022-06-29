package vf.arbiter.core.database.factory.invoice

import utopia.flow.datastructure.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.invoice.InvoicePaymentData
import vf.arbiter.core.model.stored.invoice.InvoicePayment

/**
  * Used for reading invoice payment data from the DB
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
// FIXME: Should be timestamped
object InvoicePaymentFactory extends FromValidatedRowModelFactory[InvoicePayment]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = CoreTables.invoicePayment
	
	override def fromValidatedModel(valid: Model) = 
		InvoicePayment(valid("id").getInt, InvoicePaymentData(valid("invoiceId").getInt, 
			valid("date").getLocalDate, valid("receivedAmount").getDouble, valid("remarks").string, 
			valid("created").getInstant))
}

