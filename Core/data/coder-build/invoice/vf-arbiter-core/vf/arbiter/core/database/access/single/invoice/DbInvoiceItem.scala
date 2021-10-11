package vf.arbiter.core.database.access.single.invoice

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.invoice.InvoiceItemFactory
import vf.arbiter.core.database.model.invoice.InvoiceItemModel
import vf.arbiter.core.model.stored.invoice.InvoiceItem

/**
  * Used for accessing individual InvoiceItems
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
object DbInvoiceItem extends SingleRowModelAccess[InvoiceItem] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoiceItemModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceItemFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted InvoiceItem instance
	  * @return An access point to that InvoiceItem
	  */
	def apply(id: Int) = new DbSingleInvoiceItem(id)
	
	
	// NESTED	--------------------
	
	class DbSingleInvoiceItem(val id: Int) extends UniqueInvoiceItemAccess with UniqueModelAccess[InvoiceItem]
	{
		// IMPLEMENTED	--------------------
		
		override def condition = index <=> id
	}
}

