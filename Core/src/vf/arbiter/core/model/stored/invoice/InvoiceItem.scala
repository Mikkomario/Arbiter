package vf.arbiter.core.model.stored.invoice

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.invoice.DbSingleInvoiceItem
import vf.arbiter.core.model.combined.company.FullCompanyProduct
import vf.arbiter.core.model.combined.invoice.FullInvoiceItem
import vf.arbiter.core.model.partial.invoice.InvoiceItemData

/**
  * Represents a InvoiceItem that has already been stored in the database
  * @param id id of this InvoiceItem in the database
  * @param data Wrapped InvoiceItem data
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class InvoiceItem(id: Int, data: InvoiceItemData) extends StoredModelConvertible[InvoiceItemData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this InvoiceItem in the database
	  */
	def access = DbSingleInvoiceItem(id)
	
	
	// OTHER    ---------------------
	
	/**
	 * @param product Product information
	 * @return This item with that product information included
	 */
	def +(product: FullCompanyProduct) = FullInvoiceItem(this, product)
}

