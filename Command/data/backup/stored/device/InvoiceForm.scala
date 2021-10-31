package vf.arbiter.command.model.stored.device

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.command.database.access.single.device.DbSingleInvoiceForm
import vf.arbiter.command.model.partial.device.InvoiceFormData

/**
  * Represents a InvoiceForm that has already been stored in the database
  * @param id id of this InvoiceForm in the database
  * @param data Wrapped InvoiceForm data
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
case class InvoiceForm(id: Int, data: InvoiceFormData) extends StoredModelConvertible[InvoiceFormData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this InvoiceForm in the database
	  */
	def access = DbSingleInvoiceForm(id)
}

