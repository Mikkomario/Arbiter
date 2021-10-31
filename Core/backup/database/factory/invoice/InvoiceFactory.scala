package vf.arbiter.core.database.factory.invoice

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.time.Days
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.database.model.invoice.InvoiceModel
import vf.arbiter.core.model.partial.invoice.InvoiceData
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * Used for reading Invoice data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object InvoiceFactory 
	extends FromValidatedRowModelFactory[Invoice] with FromRowFactoryWithTimestamps[Invoice] with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def creationTimePropertyName = "created"
	
	override def nonDeprecatedCondition = InvoiceModel.nonDeprecatedCondition
	
	override def table = CoreTables.invoice
	
	override def fromValidatedModel(valid: Model[Constant]) = 
		Invoice(valid("id").getInt, InvoiceData(valid("senderCompanyDetailsId").getInt, 
			valid("recipientCompanyDetailsId").getInt, valid("senderBankAccountId").getInt, 
			valid("languageId").getInt, valid("referenceCode").getString, 
			Days(valid("paymentDurationDays").getInt), valid("productDeliveryDate").localDate,
			valid("creatorId").int, valid("created").getInstant, valid("cancelledAfter").instant))
}

