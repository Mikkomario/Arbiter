package vf.arbiter.core.model.combined.invoice

import utopia.flow.view.template.Extender
import vf.arbiter.core.model.combined.company.FullCompanyProduct
import vf.arbiter.core.model.partial.invoice.InvoiceItemData
import vf.arbiter.core.model.stored.invoice.InvoiceItem

/**
 * Combines product information to an invoice item
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
case class FullInvoiceItem(item: InvoiceItem, product: FullCompanyProduct) extends Extender[InvoiceItemData]
{
	// COMPUTED --------------------------------
	
	/**
	 * @return Id of this invoice item
	 */
	def id = item.id
	
	/**
	 * @return Unit used with this invoice item
	 */
	def unit = product.unit
	
	/**
	 * @return Tax modifier used with this invoice item
	 */
	def taxModifier = product.taxModifier
	/**
	 * @return Total amount of tax to apply on top of this item's price
	 */
	def tax = item.price * taxModifier
	/**
	 * @return The price of this item, including taxes
	 */
	def totalPrice = item.price * (1 + taxModifier)
	
	
	// IMPLEMENTED  ----------------------------
	
	override def wrapped = item.data
}
