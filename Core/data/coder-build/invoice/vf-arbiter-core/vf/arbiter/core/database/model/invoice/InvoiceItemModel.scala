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
  * @since 2021-10-11
  */
object InvoiceItemModel extends DataInserter[InvoiceItemModel, InvoiceItem, InvoiceItemData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains InvoiceItem invoiceId
	  */
	val invoiceIdAttName = "invoiceId"
	
	/**
	  * Name of the property that contains InvoiceItem description
	  */
	val descriptionAttName = "description"
	
	/**
	  * Name of the property that contains InvoiceItem amount
	  */
	val amountAttName = "amount"
	
	/**
	  * Name of the property that contains InvoiceItem unitId
	  */
	val unitIdAttName = "unitId"
	
	/**
	  * Name of the property that contains InvoiceItem pricePerUnit
	  */
	val pricePerUnitAttName = "pricePerUnit"
	
	/**
	  * Name of the property that contains InvoiceItem taxModifier
	  */
	val taxModifierAttName = "taxModifier"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains InvoiceItem invoiceId
	  */
	def invoiceIdColumn = table(invoiceIdAttName)
	
	/**
	  * Column that contains InvoiceItem description
	  */
	def descriptionColumn = table(descriptionAttName)
	
	/**
	  * Column that contains InvoiceItem amount
	  */
	def amountColumn = table(amountAttName)
	
	/**
	  * Column that contains InvoiceItem unitId
	  */
	def unitIdColumn = table(unitIdAttName)
	
	/**
	  * Column that contains InvoiceItem pricePerUnit
	  */
	def pricePerUnitColumn = table(pricePerUnitAttName)
	
	/**
	  * Column that contains InvoiceItem taxModifier
	  */
	def taxModifierColumn = table(taxModifierAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = InvoiceItemFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: InvoiceItemData) = 
		apply(None, Some(data.invoiceId), Some(data.description), Some(data.amount), Some(data.unitId), 
			Some(data.pricePerUnit), Some(data.taxModifier))
	
	override def complete(id: Value, data: InvoiceItemData) = InvoiceItem(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param amount Amount of items sold within the specified unit
	  * @return A model containing only the specified amount
	  */
	def withAmount(amount: Double) = apply(amount = Some(amount))
	
	/**
	  * @param description Name or description of this item
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
	  * @param taxModifier A modifier that is applied to this item's price to get the applied tax
	  * @return A model containing only the specified taxModifier
	  */
	def withTaxModifier(taxModifier: Double) = apply(taxModifier = Some(taxModifier))
	
	/**
	  * @param unitId Unit in which these items are sold
	  * @return A model containing only the specified unitId
	  */
	def withUnitId(unitId: Int) = apply(unitId = Some(unitId))
}

/**
  * Used for interacting with InvoiceItems in the database
  * @param id InvoiceItem database id
  * @param invoiceId Id of the invoice on which this item appears
  * @param description Name or description of this item
  * @param amount Amount of items sold within the specified unit
  * @param unitId Unit in which these items are sold
  * @param pricePerUnit Euro (€) price per each sold unit of this item, without taxes applied
  * @param taxModifier A modifier that is applied to this item's price to get the applied tax
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
case class InvoiceItemModel(id: Option[Int] = None, invoiceId: Option[Int] = None, 
	description: Option[String] = None, amount: Option[Double] = None, unitId: Option[Int] = None, 
	pricePerUnit: Option[Double] = None, taxModifier: Option[Double] = None) 
	extends StorableWithFactory[InvoiceItem]
{
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceItemModel.factory
	
	override def valueProperties = 
	{
		import InvoiceItemModel._
		Vector("id" -> id, invoiceIdAttName -> invoiceId, descriptionAttName -> description, 
			amountAttName -> amount, unitIdAttName -> unitId, pricePerUnitAttName -> pricePerUnit, 
			taxModifierAttName -> taxModifier)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param amount A new amount
	  * @return A new copy of this model with the specified amount
	  */
	def withAmount(amount: Double) = copy(amount = Some(amount))
	
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
	  * @param taxModifier A new taxModifier
	  * @return A new copy of this model with the specified taxModifier
	  */
	def withTaxModifier(taxModifier: Double) = copy(taxModifier = Some(taxModifier))
	
	/**
	  * @param unitId A new unitId
	  * @return A new copy of this model with the specified unitId
	  */
	def withUnitId(unitId: Int) = copy(unitId = Some(unitId))
}

