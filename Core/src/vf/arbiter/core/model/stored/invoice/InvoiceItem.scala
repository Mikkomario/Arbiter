package vf.arbiter.core.model.stored.invoice

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.model.partial.invoice.InvoiceItemData

/**
  * Represents a InvoiceItem that has already been stored in the database
  * @param id id of this InvoiceItem in the database
  * @param data Wrapped InvoiceItem data
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class InvoiceItem(id: Int, data: InvoiceItemData) extends StoredModelConvertible[InvoiceItemData]

