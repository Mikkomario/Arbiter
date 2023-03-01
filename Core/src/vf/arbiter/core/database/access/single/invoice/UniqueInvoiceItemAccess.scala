package vf.arbiter.core.database.access.single.invoice

import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.invoice.InvoiceItemFactory
import vf.arbiter.core.database.model.invoice.InvoiceItemModel
import vf.arbiter.core.model.stored.invoice.InvoiceItem

/**
  * A common trait for access points that return individual and distinct invoice items.
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
trait UniqueInvoiceItemAccess 
	extends SingleRowModelAccess[InvoiceItem] 
		with DistinctModelAccess[InvoiceItem, Option[InvoiceItem], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the invoice on which this item appears. None if no instance (or value) was found.
	  */
	def invoiceId(implicit connection: Connection) = pullColumn(model.invoiceIdColumn).int
	
	/**
	  * Id of the type of product this item represents / is. None if no instance (or value) was found.
	  */
	def productId(implicit connection: Connection) = pullColumn(model.productIdColumn).int
	
	/**
	  * Name or description of this item (in the same language the invoice is given in). None if
	  *  no instance (or value) was found.
	  */
	def description(implicit connection: Connection) = pullColumn(model.descriptionColumn).string
	
	/**
	  * Euro (€) price per each sold unit of this item, 
	  * without taxes applied. None if no instance (or value) was found.
	  */
	def pricePerUnit(implicit connection: Connection) = pullColumn(model.pricePerUnitColumn).double
	
	/**
	  * Amount of items sold in the product's unit. None if no instance (or value) was found.
	  */
	def unitsSold(implicit connection: Connection) = pullColumn(model.unitsSoldColumn).double
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoiceItemModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceItemFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the descriptions of the targeted invoice items
	  * @param newDescription A new description to assign
	  * @return Whether any invoice item was affected
	  */
	def description_=(newDescription: String)(implicit connection: Connection) = 
		putColumn(model.descriptionColumn, newDescription)
	
	/**
	  * Updates the invoices ids of the targeted invoice items
	  * @param newInvoiceId A new invoice id to assign
	  * @return Whether any invoice item was affected
	  */
	def invoiceId_=(newInvoiceId: Int)(implicit connection: Connection) = 
		putColumn(model.invoiceIdColumn, newInvoiceId)
	
	/**
	  * Updates the per unit prices of the targeted invoice items
	  * @param newPricePerUnit A new price per unit to assign
	  * @return Whether any invoice item was affected
	  */
	def pricePerUnit_=(newPricePerUnit: Double)(implicit connection: Connection) = 
		putColumn(model.pricePerUnitColumn, newPricePerUnit)
	
	/**
	  * Updates the product ids of the targeted invoice items
	  * @param newProductId A new product id to assign
	  * @return Whether any invoice item was affected
	  */
	def productId_=(newProductId: Int)(implicit connection: Connection) = 
		putColumn(model.productIdColumn, newProductId)
	
	/**
	  * Updates the units sold of the targeted invoice items
	  * @param newUnitsSold A new units sold to assign
	  * @return Whether any invoice item was affected
	  */
	def unitsSold_=(newUnitsSold: Double)(implicit connection: Connection) = 
		putColumn(model.unitsSoldColumn, newUnitsSold)
}

