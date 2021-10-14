package vf.arbiter.core.model.partial.invoice

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._

/**
  * Represents an individual sold item or service
  * @param invoiceId Id of the invoice on which this item appears
  * @param productId Id of the type of product this item represents / is
  * @param description Name or description of this item (in the same language the invoice is given in)
  * @param pricePerUnit Euro (€) price per each sold unit of this item, without taxes applied
  * @param unitsSold Amount of items sold in the product's unit
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class InvoiceItemData(invoiceId: Int, productId: Int, description: String, pricePerUnit: Double, 
	unitsSold: Double = 1.0) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("invoice_id" -> invoiceId, "product_id" -> productId, "description" -> description, 
			"price_per_unit" -> pricePerUnit, "units_sold" -> unitsSold))
}

