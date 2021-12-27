package vf.arbiter.core.database.factory.invoice

import utopia.flow.datastructure.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.invoice.InvoiceItemData
import vf.arbiter.core.model.stored.invoice.InvoiceItem

/**
  * Used for reading InvoiceItem data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object InvoiceItemFactory extends FromValidatedRowModelFactory[InvoiceItem]
{
	// IMPLEMENTED	--------------------
	
	override def table = CoreTables.invoiceItem
	
	override def defaultOrdering = None
	
	override def fromValidatedModel(valid: Model) =
		InvoiceItem(valid("id").getInt, InvoiceItemData(valid("invoiceId").getInt, valid("productId").getInt, 
			valid("description").getString, valid("pricePerUnit").getDouble, valid("unitsSold").getDouble))
}

