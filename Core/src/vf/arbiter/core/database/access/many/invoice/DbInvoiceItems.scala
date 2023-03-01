package vf.arbiter.core.database.access.many.invoice

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.SqlExtensions._

/**
  * The root access point when targeting multiple InvoiceItems at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbInvoiceItems extends ManyInvoiceItemsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted InvoiceItems
	  * @return An access point to InvoiceItems with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbInvoiceItemsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbInvoiceItemsSubset(targetIds: Set[Int]) extends ManyInvoiceItemsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

