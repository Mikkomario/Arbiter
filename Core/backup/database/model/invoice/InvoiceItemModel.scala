package vf.arbiter.core.database.model.invoice

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.invoice.InvoiceItemFactory
import vf.arbiter.core.model.partial.invoice.InvoiceItemData
import vf.arbiter.core.model.stored.invoice.InvoiceItem

/**
  * Used for constructing InvoiceItemModel instances and for inserting InvoiceItems to the database
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object InvoiceItemModel extends DataInserter[InvoiceItemModel, InvoiceItem, InvoiceItemData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains InvoiceItem invoiceId
	  */
	val invoiceIdAttName = "invoiceId"
	
	/**
	  * Name of the property that contains InvoiceItem productId
	  */
	val productIdAttName = "productId"
	
	/**
	  * Name of the property that contains InvoiceItem description
	  */
	val descriptionAttName = "description"
	
	/**
	  * Name of the property that contains InvoiceItem pricePerUnit
	  */
	val pricePerUnitAttName = "pricePerUnit"
	
	/**
	  * Name of the property that contains InvoiceItem unitsSold
	  */
	val unitsSoldAttName = "unitsSold"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains InvoiceItem invoiceId
	  */
	def invoiceIdColumn = table(invoiceIdAttName)
	
	/**
	  * Column that contains InvoiceItem productId
	  */
	def productIdColumn = table(productIdAttName)
	
	/**
	  * Column that contains InvoiceItem description
	  */
	def descriptionColumn = table(descriptionAttName)
	
	/**
	  * Column that contains InvoiceItem pricePerUnit
	  */
	def pricePerUnitColumn = table(pricePerUnitAttName)
	
	/**
	  * Column that contains InvoiceItem unitsSold
	  */
	def unitsSoldColumn = table(unitsSoldAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = InvoiceItemFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: InvoiceItemData) = 
		apply(None, Some(data.invoiceId), Some(data.productId), Some(data.description), 
			Some(data.pricePerUnit), Some(data.unitsSold))
	
	override def complete(id: Value, data: InvoiceItemData) = InvoiceItem(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param description Name or description of this item (in the same language the invoice is given in)
	  * @return A model containing only the specified description
	  */
	def withDescription(description: String) = apply(description = Some(description))
	
	/**
	  * @param id A InvoiceItem id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param invoiceId Id of the invoice on which this item appears
	  * @return A model containing only the specified invoiceId
	  */
	def withInvoiceId(invoiceId: Int) = apply(invoiceId = Some(invoiceId))
	
	/**
	  * @param pricePerUnit Euro (€) price per each sold unit of this item, without taxes applied
	  * @return A model containing only the specified pricePerUnit
	  */
	def withPricePerUnit(pricePerUnit: Double) = apply(pricePerUnit = Some(pricePerUnit))
	
	/**
	  * @param productId Id of the type of product this item represents / is
	  * @return A model containing only the specified productId
	  */
	def withProductId(productId: Int) = apply(productId = Some(productId))
	
	/**
	  * @param unitsSold Amount of items sold in the product's unit
	  * @return A model containing only the specified unitsSold
	  */
	def withUnitsSold(unitsSold: Double) = apply(unitsSold = Some(unitsSold))
}

/**
  * Used for interacting with InvoiceItems in the database
  * @param id InvoiceItem database id
  * @param invoiceId Id of the invoice on which this item appears
  * @param productId Id of the type of product this item represents / is
  * @param description Name or description of this item (in the same language the invoice is given in)
  * @param pricePerUnit Euro (€) price per each sold unit of this item, without taxes applied
  * @param unitsSold Amount of items sold in the product's unit
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class InvoiceItemModel(id: Option[Int] = None, invoiceId: Option[Int] = None, 
	productId: Option[Int] = None, description: Option[String] = None, pricePerUnit: Option[Double] = None, 
	unitsSold: Option[Double] = None) 
	extends StorableWithFactory[InvoiceItem]
{
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceItemModel.factory
	
	override def valueProperties = 
	{
		import InvoiceItemModel._
		Vector("id" -> id, invoiceIdAttName -> invoiceId, productIdAttName -> productId, 
			descriptionAttName -> description, pricePerUnitAttName -> pricePerUnit, 
			unitsSoldAttName -> unitsSold)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param description A new description
	  * @return A new copy of this model with the specified description
	  */
	def withDescription(description: String) = copy(description = Some(description))
	
	/**
	  * @param invoiceId A new invoiceId
	  * @return A new copy of this model with the specified invoiceId
	  */
	def withInvoiceId(invoiceId: Int) = copy(invoiceId = Some(invoiceId))
	
	/**
	  * @param pricePerUnit A new pricePerUnit
	  * @return A new copy of this model with the specified pricePerUnit
	  */
	def withPricePerUnit(pricePerUnit: Double) = copy(pricePerUnit = Some(pricePerUnit))
	
	/**
	  * @param productId A new productId
	  * @return A new copy of this model with the specified productId
	  */
	def withProductId(productId: Int) = copy(productId = Some(productId))
	
	/**
	  * @param unitsSold A new unitsSold
	  * @return A new copy of this model with the specified unitsSold
	  */
	def withUnitsSold(unitsSold: Double) = copy(unitsSold = Some(unitsSold))
}

