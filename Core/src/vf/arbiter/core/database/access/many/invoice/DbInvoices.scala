package vf.arbiter.core.database.access.many.invoice

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * The root access point when targeting multiple invoices at a time
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
object DbInvoices extends ManyInvoicesAccess with NonDeprecatedView[Invoice]
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted invoices
	  * @return An access point to invoices with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbInvoicesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbInvoicesSubset(targetIds: Set[Int]) extends ManyInvoicesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

