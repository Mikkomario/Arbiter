package vf.arbiter.core.model.combined.company

import utopia.flow.view.template.Extender
import vf.arbiter.core.model.combined.invoice.DescribedItemUnit

/**
 * Combines unit and descriptions data to company product data
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
case class FullCompanyProduct(describedProduct: DescribedCompanyProduct, unit: DescribedItemUnit)
	extends Extender[DescribedCompanyProduct]
{
	// COMPUTED --------------------------------
	
	/**
	 * @return Id of this product
	 */
	def id = describedProduct.id
	
	/**
	 * @return Product information
	 */
	def product = describedProduct.wrapped
	
	
	// IMPLEMENTED  ----------------------------
	
	override def wrapped = describedProduct
}