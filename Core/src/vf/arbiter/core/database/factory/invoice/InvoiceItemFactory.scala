package vf.arbiter.core.database.factory.invoice

import utopia.flow.datastructure.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.invoice.InvoiceItemData
import vf.arbiter.core.model.stored.invoice.InvoiceItem

/**
  * Used for reading invoice item data from the DB
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
object InvoiceItemFactory extends FromValidatedRowModelFactory[InvoiceItem]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = CoreTables.invoiceItem
	
	override def fromValidatedModel(valid: Model) = 
		InvoiceItem(valid("id").getInt, InvoiceItemData(valid("invoiceId").getInt, valid("productId").getInt, 
			valid("description").getString, valid("pricePerUnit").getDouble, valid("unitsSold").getDouble))
}

