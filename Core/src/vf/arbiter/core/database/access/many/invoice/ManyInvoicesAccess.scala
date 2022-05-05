package vf.arbiter.core.database.access.many.invoice

import utopia.flow.datastructure.immutable.Pair

import java.time.{Instant, LocalDate, Year}
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Days
import utopia.flow.time.TimeExtensions._
import utopia.metropolis.model.cached.LanguageIds
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{ChronoRowFactoryView, SubView}
import utopia.vault.sql.{Condition, Select, Where}
import utopia.vault.sql.SqlExtensions._
import vf.arbiter.core.database.access.many.company.{DbCompanies, DbFullCompanyBankAccounts, DbManyCompanyDetails}
import vf.arbiter.core.database.factory.invoice.InvoiceFactory
import vf.arbiter.core.database.model.company.CompanyDetailsModel
import vf.arbiter.core.database.model.invoice.InvoiceModel
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
 * A common trait for access points which target multiple Invoices at a time
 * @author Mikko Hilpinen
 * @since 2021-10-14
 */
trait ManyInvoicesAccess
	extends ManyRowModelAccess[Invoice] with Indexed with ChronoRowFactoryView[Invoice, ManyInvoicesAccess]
{
	// COMPUTED	--------------------
	
	/**
	 * senderCompanyDetailsIds of the accessible Invoices
	 */
	def senderCompanyDetailsIds(implicit connection: Connection) =
		pullColumn(model.senderCompanyDetailsIdColumn).flatMap { value => value.int }
	/**
	 * recipientCompanyDetailsIds of the accessible Invoices
	 */
	def recipientCompanyDetailsIds(implicit connection: Connection) =
		pullColumn(model.recipientCompanyDetailsIdColumn).flatMap { value => value.int }
	/**
	 * senderBankAccountIds of the accessible Invoices
	 */
	def senderBankAccountIds(implicit connection: Connection) =
		pullColumn(model.senderBankAccountIdColumn).flatMap { value => value.int }
	/**
	 * languageIds of the accessible Invoices
	 */
	def languageIds(implicit connection: Connection) =
		pullColumn(model.languageIdColumn).flatMap { value => value.int }
	/**
	 * referenceCodes of the accessible Invoices
	 */
	def referenceCodes(implicit connection: Connection) =
		pullColumn(model.referenceCodeColumn).flatMap { value => value.string }
	/**
	 * paymentDuration of the accessible Invoices
	 */
	def paymentDuration(implicit connection: Connection) =
		pullColumn(model.paymentDurationColumn).flatMap { value => value.int.map { Days(_) } }
	/**
	 * productDeliveryDates of the accessible Invoices
	 */
	def productDeliveryDates(implicit connection: Connection) =
		pullColumn(model.productDeliveryDateColumn).flatMap { value => value.localDate }
	/**
	 * creatorIds of the accessible Invoices
	 */
	def creatorIds(implicit connection: Connection) =
		pullColumn(model.creatorIdColumn).flatMap { value => value.int }
	/**
	 * createds of the accessible Invoices
	 */
	def createds(implicit connection: Connection) =
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	/**
	 * cancelledAfters of the accessible Invoices
	 */
	def cancelledAfters(implicit connection: Connection) =
		pullColumn(model.cancelledAfterColumn).flatMap { value => value.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	 * Factory used for constructing database the interaction models
	 */
	protected def model = InvoiceModel
	/**
	 * @return Factory used for constructing database interaction models for company details
	 */
	protected def companyDetailsModel = CompanyDetailsModel
	
	/**
	 * @param connection Implicit DB connection
	 * @param languageIds Implicit language ids
	 * @return Fully detailed copies of all accessible invoices
	 */
	def full(implicit connection: Connection, languageIds: LanguageIds) = complete(pull).toVector
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceFactory
	
	override def filter(additionalCondition: Condition): ManyInvoicesAccess =
		new ManyInvoicesAccess.ManyInvoicesSubView(this, additionalCondition)
	
	
	// OTHER	--------------------
	
	/**
	 * @param year Targeted year
	 * @return A copy of this access point, limited to invoices created during that year
	 */
	def during(year: Year) =
		filter(model.createdColumn.isBetween(year.firstDay.atStartOfDay(),
			year.lastDay.tomorrow.atStartOfDay()))
	
	/**
	 * @param senderCompanyId Id of the company sending invoices
	 * @param connection Implicit DB Connection
	 * @return All accessible invoices sent by that company
	 */
	def sentByCompanyWithId(senderCompanyId: Int)(implicit connection: Connection) = {
		val detailsModel = CompanyDetailsModel
		// Joins to company details table in order to acquire all company-related invoices
		factory(connection(Select.tables(target.joinFrom(model.senderCompanyDetailsIdColumn), factory.tables) +
			Where(mergeCondition(detailsModel.withCompanyId(senderCompanyId).toCondition))))
	}
	
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
			Where(mergeCondition(companyDetailsModel.withCompanyId(senderCompanyId)))).rowIntValues
		factory(connection(Select(target.joinFrom(model.recipientCompanyDetailsIdColumn), table) +
			Where(companyDetailsModel.withCompanyId(recipientCompanyId).toCondition && index.in(invoiceIds))))
	}
	
	/**
	 * Completes the specified invoices by adding company, bank & item data
	 * @param invoices Invoices to complete
	 * @param connection Implicit DB Connection
	 * @param languageIds Implicit language ids
	 * @return Fully detailed copies of the specified invoices
	 */
	def complete(invoices: Iterable[Invoice])(implicit connection: Connection, languageIds: LanguageIds) =
	{
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
	 * Updates the cancelledAfter of the targeted Invoice instance(s)
	 * @param newCancelledAfter A new cancelledAfter to assign
	 * @return Whether any Invoice instance was affected
	 */
	def cancelledAfter_=(newCancelledAfter: Instant)(implicit connection: Connection) =
		putColumn(model.cancelledAfterColumn, newCancelledAfter)
	
	/**
	 * Updates the created of the targeted Invoice instance(s)
	 * @param newCreated A new created to assign
	 * @return Whether any Invoice instance was affected
	 */
	def created_=(newCreated: Instant)(implicit connection: Connection) =
		putColumn(model.createdColumn, newCreated)
	
	/**
	 * Updates the creatorId of the targeted Invoice instance(s)
	 * @param newCreatorId A new creatorId to assign
	 * @return Whether any Invoice instance was affected
	 */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) =
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	 * Updates the languageId of the targeted Invoice instance(s)
	 * @param newLanguageId A new languageId to assign
	 * @return Whether any Invoice instance was affected
	 */
	def languageId_=(newLanguageId: Int)(implicit connection: Connection) =
		putColumn(model.languageIdColumn, newLanguageId)
	
	/**
	 * Updates the paymentDuration of the targeted Invoice instance(s)
	 * @param newPaymentDuration A new paymentDuration to assign
	 * @return Whether any Invoice instance was affected
	 */
	def paymentDuration_=(newPaymentDuration: Days)(implicit connection: Connection) =
		putColumn(model.paymentDurationColumn, newPaymentDuration.length)
	
	/**
	 * Updates the productDeliveryDate of the targeted Invoice instance(s)
	 * @param newProductDeliveryDate A new productDeliveryDate to assign
	 * @return Whether any Invoice instance was affected
	 */
	def productDeliveryDate_=(newProductDeliveryDate: LocalDate)(implicit connection: Connection) =
		putColumn(model.productDeliveryDateColumn, newProductDeliveryDate)
	
	/**
	 * Updates the recipientCompanyDetailsId of the targeted Invoice instance(s)
	 * @param newRecipientCompanyDetailsId A new recipientCompanyDetailsId to assign
	 * @return Whether any Invoice instance was affected
	 */
	def recipientCompanyDetailsId_=(newRecipientCompanyDetailsId: Int)(implicit connection: Connection) =
		putColumn(model.recipientCompanyDetailsIdColumn, newRecipientCompanyDetailsId)
	
	/**
	 * Updates the referenceCode of the targeted Invoice instance(s)
	 * @param newReferenceCode A new referenceCode to assign
	 * @return Whether any Invoice instance was affected
	 */
	def referenceCode_=(newReferenceCode: String)(implicit connection: Connection) =
		putColumn(model.referenceCodeColumn, newReferenceCode)
	
	/**
	 * Updates the senderBankAccountId of the targeted Invoice instance(s)
	 * @param newSenderBankAccountId A new senderBankAccountId to assign
	 * @return Whether any Invoice instance was affected
	 */
	def senderBankAccountId_=(newSenderBankAccountId: Int)(implicit connection: Connection) =
		putColumn(model.senderBankAccountIdColumn, newSenderBankAccountId)
	
	/**
	 * Updates the senderCompanyDetailsId of the targeted Invoice instance(s)
	 * @param newSenderCompanyDetailsId A new senderCompanyDetailsId to assign
	 * @return Whether any Invoice instance was affected
	 */
	def senderCompanyDetailsId_=(newSenderCompanyDetailsId: Int)(implicit connection: Connection) =
		putColumn(model.senderCompanyDetailsIdColumn, newSenderCompanyDetailsId)
}

