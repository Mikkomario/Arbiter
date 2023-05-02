package vf.arbiter.core.database.access.many.invoice

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.metropolis.model.cached.LanguageIds
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.{ChronoRowFactoryView, SubView}
import utopia.vault.sql.{Condition, Select, Where}
import vf.arbiter.core.database.access.many.company.{DbCompanies, DbFullCompanyBankAccounts, DbManyCompanyDetails}
import vf.arbiter.core.database.factory.invoice.InvoiceFactory
import vf.arbiter.core.database.model.company.CompanyDetailsModel
import vf.arbiter.core.model.combined.invoice.FullInvoice
import vf.arbiter.core.model.stored.invoice.Invoice

object ManyInvoicesAccess
{
	// NESTED	--------------------
	
	private class ManyInvoicesSubView(override val parent: ManyRowModelAccess[Invoice], 
		override val filterCondition: Condition) 
		extends ManyInvoicesAccess with SubView
}

/**
  * A common trait for access points which target multiple invoices at a time
  * @author Mikko Hilpinen
  * @since 14.10.2021, v1.3
  */
trait ManyInvoicesAccess 
	extends ManyInvoicesAccessLike[Invoice, ManyInvoicesAccess] with ManyRowModelAccess[Invoice] 
		with ChronoRowFactoryView[Invoice, ManyInvoicesAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * Fully detailed copies of all accessible invoices
	  * @param connection Implicit DB connection
	  * @param languageIds Implicit language ids
	  */
	def full(implicit connection: Connection, languageIds: LanguageIds) = complete(pull).toVector
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override def factory = InvoiceFactory
	
	override def filter(additionalCondition: Condition): ManyInvoicesAccess = 
		new ManyInvoicesAccess.ManyInvoicesSubView(this, additionalCondition)
	
	
	// OTHER	--------------------
	
	/**
	  * Completes the specified invoices by adding company, bank & item data
	  * @param invoices Invoices to complete
	  * @param connection Implicit DB Connection
	  * @param languageIds Implicit language ids
	  * @return Fully detailed copies of the specified invoices
	  */
	def complete(invoices: Iterable[Invoice])(implicit connection: Connection, languageIds: LanguageIds) = {
		if (invoices.isEmpty)
			Vector()
		else {
			// Collects invoice ids, then searches for linked data
			val invoiceIds = invoices.map { _.id }.toSet
			val itemsByInvoiceId = DbInvoiceItems.forAnyOfInvoices(invoiceIds).full.groupBy { _.invoiceId }
			// Also fetches company data
			val companyDetailsIds = invoices
				.flatMap { i => Pair(i.senderCompanyDetailsId, i.recipientCompanyDetailsId) }.toSet
			val fullDetails = DbManyCompanyDetails(companyDetailsIds).full.pull
			val companyIds = fullDetails.map { _.companyId }.toSet
			val companyById = DbCompanies(companyIds).pull.map { c => c.id -> c }.toMap
			val fullCompanyByDetailsId = fullDetails.map { d => d.id -> (companyById(d.companyId) + d) }.toMap
			// Finally collects bank data
			val bankAccountIds = invoices.map { _.senderBankAccountId }.toSet
			val bankAccountPerId = DbFullCompanyBankAccounts(bankAccountIds).pull.map { a => a.id -> a }.toMap
			// Combines the data together
			invoices.map { i => FullInvoice(i, fullCompanyByDetailsId(i.senderCompanyDetailsId),
				fullCompanyByDetailsId(i.recipientCompanyDetailsId), bankAccountPerId(i.senderBankAccountId),
				itemsByInvoiceId.getOrElse(i.id, Vector())) }
		}
	}
	
	/**
	 * Finds invoices that are made between the two mentioned companies
	 * @param senderCompanyId Id of the company who sent the invoice
	 * @param recipientCompanyId Id of the company who received the invoice
	 * @param connection Implicit DB connection
	 * @return Invoices from the sender company to the recipient company
	 */
	def betweenCompanies(senderCompanyId: Int, recipientCompanyId: Int)(implicit connection: Connection) = {
		// Unfortunately, because having to join to the same table twice, has to perform two separate searches
		val invoiceIds = connection(Select(target.joinFrom(model.senderCompanyDetailsIdColumn), index) +
			Where(mergeCondition(companyDetailsModel.withCompanyId(senderCompanyId)))).rowIntValues
		factory(connection(Select(target.joinFrom(model.recipientCompanyDetailsIdColumn), table) +
			Where(companyDetailsModel.withCompanyId(recipientCompanyId).toCondition && index.in(invoiceIds))))
	}
	
	/**
	  * @param senderCompanyId Id of the company sending invoices
	  * @param connection Implicit DB Connection
	  * @return All accessible invoices sent by that company
	  */
	def sentByCompanyWithId(senderCompanyId: Int)(implicit connection: Connection) = {
		val detailsModel = CompanyDetailsModel
		// Joins to company details table in order to acquire all company-related invoices
		factory(connection(Select.tables(target.joinFrom(model.senderCompanyDetailsIdColumn), 
			factory.tables) +
			Where(mergeCondition(detailsModel.withCompanyId(senderCompanyId).toCondition))))
	}
}

