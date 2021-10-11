package vf.arbiter.core.database.factory.invoice

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.time.Days
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.invoice.InvoiceData
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * Used for reading Invoice data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
object InvoiceFactory extends FromValidatedRowModelFactory[Invoice] with FromRowFactoryWithTimestamps[Invoice]
{
	// IMPLEMENTED	--------------------
	
	override def creationTimePropertyName = "created"
	
	override def table = CoreTables.invoice
	
	override def fromValidatedModel(valid: Model[Constant]) = 
		Invoice(valid("id").getInt, InvoiceData(valid("senderCompanyId").getInt, 
			valid("recipientCompanyId").getInt, valid("referenceCode").getString, 
			valid("created").getInstant, valid("creatorId").getInt, 
			Days(valid("paymentDurationDays").getInt), valid("productDeliveryDate").localDate))
}

