package vf.arbiter.core.database.access.many.invoice

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.view.NonDeprecatedView
import utopia.vault.sql.SqlExtensions._
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * The root access point when targeting multiple Invoices at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbInvoices extends ManyInvoicesAccess with NonDeprecatedView[Invoice]
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted Invoices
	  * @return An access point to Invoices with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbInvoicesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbInvoicesSubset(targetIds: Set[Int]) extends ManyInvoicesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

