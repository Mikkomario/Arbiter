package vf.arbiter.core.database.factory.invoice

import utopia.vault.nosql.factory.multi.MultiCombiningFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.core.model.combined.invoice.InvoiceWithItems
import vf.arbiter.core.model.stored.invoice.{Invoice, InvoiceItem}

/**
  * Used for reading invoice with itemss from the database
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
object InvoiceWithItemsFactory 
	extends MultiCombiningFactory[InvoiceWithItems, Invoice, InvoiceItem] with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = InvoiceItemFactory
	
	override def isAlwaysLinked = false
	
	override def nonDeprecatedCondition = parentFactory.nonDeprecatedCondition
	
	override def parentFactory = InvoiceFactory
	
	override def apply(invoice: Invoice, items: Vector[InvoiceItem]) = InvoiceWithItems(invoice, items)
}

