package vf.arbiter.core.database.access.many.invoice

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.SqlExtensions._

/**
  * The root access point when targeting multiple InvoicePayments at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbInvoicePayments extends ManyInvoicePaymentsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted InvoicePayments
	  * @return An access point to InvoicePayments with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbInvoicePaymentsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbInvoicePaymentsSubset(targetIds: Set[Int]) extends ManyInvoicePaymentsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

