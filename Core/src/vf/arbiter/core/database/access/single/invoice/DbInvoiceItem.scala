package vf.arbiter.core.database.access.single.invoice

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.invoice.InvoiceItemFactory
import vf.arbiter.core.database.model.invoice.InvoiceItemModel
import vf.arbiter.core.model.stored.invoice.InvoiceItem

/**
  * Used for accessing individual invoice items
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
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
	  * @param id Database id of the targeted invoice item
	  * @return An access point to that invoice item
	  */
	def apply(id: Int) = DbSingleInvoiceItem(id)
}

