package vf.arbiter.core.model.partial.invoice

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._

/**
  * Represents an individual sold item or service
  * @param invoiceId Id of the invoice on which this item appears
  * @param description Name or description of this item
  * @param amount Amount of items sold within the specified unit
  * @param unitId Unit in which these items are sold
  * @param pricePerUnit Euro (€) price per each sold unit of this item, without taxes applied
  * @param taxModifier A modifier that is applied to this item's price to get the applied tax
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
case class InvoiceItemData(invoiceId: Int, description: String, amount: Double = 1.0, unitId: Int, 
	pricePerUnit: Double, taxModifier: Double = 0.24) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("invoice_id" -> invoiceId, "description" -> description, "amount" -> amount, 
			"unit_id" -> unitId, "price_per_unit" -> pricePerUnit, "tax_modifier" -> taxModifier))
}

