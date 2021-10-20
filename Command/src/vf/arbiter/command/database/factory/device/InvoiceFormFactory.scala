package vf.arbiter.command.database.factory.device

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.util.FileExtensions._
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.command.database.CommandTables
import vf.arbiter.command.model.partial.device.InvoiceFormData
import vf.arbiter.command.model.stored.device.InvoiceForm

/**
  * Used for reading InvoiceForm data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
object InvoiceFormFactory extends FromValidatedRowModelFactory[InvoiceForm]
{
	// IMPLEMENTED	--------------------
	
	override def table = CommandTables.invoiceForm
	
	override def fromValidatedModel(valid: Model[Constant]) = 
		InvoiceForm(valid("id").getInt, InvoiceFormData(valid("OwnerId").getInt, valid("LanguageId").getInt, 
			valid("CompanyId").int, valid("Path").getString))
}

