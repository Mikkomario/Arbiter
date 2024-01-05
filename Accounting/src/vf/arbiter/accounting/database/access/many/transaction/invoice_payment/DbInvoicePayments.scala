package vf.arbiter.accounting.database.access.many.transaction.invoice_payment

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple invoice payments at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbInvoicePayments extends ManyInvoicePaymentsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted invoice payments
	  * @return An access point to invoice payments with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbInvoicePaymentsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbInvoicePaymentsSubset(targetIds: Set[Int]) extends ManyInvoicePaymentsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

