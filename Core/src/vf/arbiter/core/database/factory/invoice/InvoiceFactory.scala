package vf.arbiter.core.database.factory.invoice

import utopia.flow.generic.model.immutable.Model
import utopia.flow.time.Days
import utopia.flow.time.TimeExtensions._
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.database.model.invoice.InvoiceModel
import vf.arbiter.core.model.partial.invoice.InvoiceData
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * Used for reading invoice data from the DB
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
object InvoiceFactory 
	extends FromValidatedRowModelFactory[Invoice] with FromRowFactoryWithTimestamps[Invoice] with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def creationTimePropertyName = "created"
	
	override def nonDeprecatedCondition = InvoiceModel.nonDeprecatedCondition
	
	override def table = CoreTables.invoice
	
	override def fromValidatedModel(valid: Model) = {
		val deliveryDates = valid("productDeliveryBegin").localDate.flatMap { begin =>
			valid("productDeliveryEnd").localDate.map { end => begin to end }
		}
		Invoice(valid("id").getInt, InvoiceData(valid("senderCompanyDetailsId").getInt, 
			valid("recipientCompanyDetailsId").getInt, valid("senderBankAccountId").getInt, 
			valid("languageId").getInt, valid("referenceCode").getString, 
			Days(valid("paymentDurationDays").getInt), deliveryDates, valid("creatorId").int,
			valid("created").getInstant, valid("cancelledAfter").instant))
	}
}

