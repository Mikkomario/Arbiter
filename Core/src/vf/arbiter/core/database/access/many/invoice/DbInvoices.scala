package vf.arbiter.core.database.access.many.invoice

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.view.NonDeprecatedView
import utopia.vault.sql.SqlExtensions._
import utopia.vault.sql.{Select, Where}
import vf.arbiter.core.database.model.company.CompanyDetailsModel
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * The root access point when targeting multiple Invoices at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbInvoices extends ManyInvoicesAccess with NonDeprecatedView[Invoice]
{
	// COMPUTED --------------------
	
	private def companyDetailsModel = CompanyDetailsModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted Invoices
	  * @return An access point to Invoices with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbInvoicesSubset(ids)
	
	/**
	 * Finds invoices that are made between the two mentioned companies
	 * @param senderCompanyId Id of the company who sent the invoice
	 * @param recipientCompanyId Id of the company who received the invoice
	 * @param connection Implicit DB connection
	 * @return Invoices from the sender company to the recipient company
	 */
	def betweenCompanies(senderCompanyId: Int, recipientCompanyId: Int)(implicit connection: Connection) =
	{
		// Unfortunately, because having to join to the same table twice, has to perform two separate searches
		val invoiceIds = connection(Select(target.joinFrom(model.senderCompanyDetailsIdColumn), index) +
			Where(companyDetailsModel.withCompanyId(senderCompanyId))).rowIntValues
		factory(connection(Select(target.joinFrom(model.recipientCompanyDetailsIdColumn), table) +
			Where(companyDetailsModel.withCompanyId(recipientCompanyId).toCondition && index.in(invoiceIds))))
	}
	
	
	// NESTED	--------------------
	
	class DbInvoicesSubset(targetIds: Set[Int]) extends ManyInvoicesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

