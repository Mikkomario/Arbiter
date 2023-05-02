package vf.arbiter.command.database.access.many.device

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{SubView, UnconditionalView}

/**
  * The root access point when targeting multiple InvoiceForms at a time
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
object DbInvoiceForms extends ManyInvoiceFormsAccess with UnconditionalView
{
	// OTHER    ----------------------------------
	
	/**
	 * @param ids Ids of the targeted forms
	 * @return An access point to those forms
	 */
	def apply(ids: Iterable[Int]) = new DbInvoiceFormsByIds(ids)
	
	
	// NESTED   ----------------------------------
	
	class DbInvoiceFormsByIds(formIds: Iterable[Int]) extends ManyInvoiceFormsAccess with SubView
	{
		override protected def parent = DbInvoiceForms
		
		override def filterCondition = index in formIds
	}
}
