package vf.arbiter.command.database.access.single.device

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.command.database.factory.device.InvoiceFormFactory
import vf.arbiter.command.database.model.device.InvoiceFormModel
import vf.arbiter.command.model.stored.device.InvoiceForm

/**
  * Used for accessing individual InvoiceForms
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
object DbInvoiceForm extends SingleRowModelAccess[InvoiceForm] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoiceFormModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceFormFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted InvoiceForm instance
	  * @return An access point to that InvoiceForm
	  */
	def apply(id: Int) = DbSingleInvoiceForm(id)
}

