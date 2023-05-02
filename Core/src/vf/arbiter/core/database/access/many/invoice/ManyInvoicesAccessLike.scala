package vf.arbiter.core.database.access.many.invoice

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.time.{Days, Now}
import utopia.flow.time.TimeExtensions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.arbiter.core.database.model.company.CompanyDetailsModel
import vf.arbiter.core.database.model.invoice.InvoiceModel

import java.time.{Instant, LocalDate, Year}

/**
  * A common trait for access points which target multiple invoices or similar instances at a time
  * @author Mikko Hilpinen
  * @since 29.06.2022, v1.3
  */
trait ManyInvoicesAccessLike[+A, +Repr <: ManyModelAccess[A]] 
	extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * sender company details ids of the accessible invoices
	  */
	def senderCompanyDetailsIds(implicit connection: Connection) = 
		pullColumn(model.senderCompanyDetailsIdColumn).map { v => v.getInt }
	
	/**
	  * recipient company details ids of the accessible invoices
	  */
	def recipientCompanyDetailsIds(implicit connection: Connection) = 
		pullColumn(model.recipientCompanyDetailsIdColumn).map { v => v.getInt }
	
	/**
	  * sender bank account ids of the accessible invoices
	  */
	def senderBankAccountIds(implicit connection: Connection) = 
		pullColumn(model.senderBankAccountIdColumn).map { v => v.getInt }
	
	/**
	  * languages ids of the accessible invoices
	  */
	def languagesIds(implicit connection: Connection) = pullColumn(model.languageIdColumn)
		.map { v => v.getInt }
	
	/**
	  * reference codes of the accessible invoices
	  */
	def referenceCodes(implicit connection: Connection) = 
		pullColumn(model.referenceCodeColumn).map { v => v.getString }
	
	/**
	  * payment duration of the accessible invoices
	  */
	def paymentDuration(implicit connection: Connection) = 
		pullColumn(model.paymentDurationColumn).map { v => Days(v.getInt) }
	
	/**
	  * product delivery begins of the accessible invoices
	  */
	def productDeliveryBegins(implicit connection: Connection) = 
		pullColumn(model.productDeliveryBeginColumn).flatMap { _.localDate }
	
	/**
	  * product delivery ends of the accessible invoices
	  */
	def productDeliveryEnds(implicit connection: Connection) = 
		pullColumn(model.productDeliveryEndColumn).flatMap { _.localDate }
	
	/**
	  * creator ids of the accessible invoices
	  */
	def creatorIds(implicit connection: Connection) = pullColumn(model.creatorIdColumn).flatMap { _.int }
	
	/**
	  * creation times of the accessible invoices
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn).map { _.getInstant }
	
	/**
	  * cancelled afters of the accessible invoices
	  */
	def cancelledAfters(implicit connection: Connection) = 
		pullColumn(model.cancelledAfterColumn).flatMap { _.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoiceModel
	
	/**
	 * Factory used for constructing database interaction models for company details
	 */
	protected def companyDetailsModel = CompanyDetailsModel
	
	
	// OTHER	--------------------
	
	/**
	 * @param year Targeted year
	 * @return A copy of this access point, limited to invoices created during that year
	 */
	def during(year: Year) =
		filter(model.createdColumn.isBetween(year.firstDay.atStartOfDay(),
			year.lastDay.tomorrow.atStartOfDay()))
	
	/**
	  * Updates the cancelled afters of the targeted invoices
	  * @param newCancelledAfter A new cancelled after to assign
	  * @return Whether any invoice was affected
	  */
	def cancelledAfters_=(newCancelledAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.cancelledAfterColumn, newCancelledAfter)
	
	/**
	  * Updates the creation times of the targeted invoices
	  * @param newCreated A new created to assign
	  * @return Whether any invoice was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted invoices
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any invoice was affected
	  */
	def creatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Deprecates all accessible invoices
	  * @return Whether any row was targeted
	  */
	def deprecate()(implicit connection: Connection) = cancelledAfters = Now
	
	/**
	  * Updates the languages ids of the targeted invoices
	  * @param newLanguageId A new language id to assign
	  * @return Whether any invoice was affected
	  */
	def languagesIds_=(newLanguageId: Int)(implicit connection: Connection) = 
		putColumn(model.languageIdColumn, newLanguageId)
	
	/**
	  * Updates the payment duration of the targeted invoices
	  * @param newPaymentDuration A new payment duration to assign
	  * @return Whether any invoice was affected
	  */
	def paymentDuration_=(newPaymentDuration: Days)(implicit connection: Connection) = 
		putColumn(model.paymentDurationColumn, newPaymentDuration.length)
	
	/**
	  * Updates the product delivery begins of the targeted invoices
	  * @param newProductDeliveryBegin A new product delivery begin to assign
	  * @return Whether any invoice was affected
	  */
	def productDeliveryBegins_=(newProductDeliveryBegin: LocalDate)(implicit connection: Connection) = 
		putColumn(model.productDeliveryBeginColumn, newProductDeliveryBegin)
	
	/**
	  * Updates the product delivery ends of the targeted invoices
	  * @param newProductDeliveryEnd A new product delivery end to assign
	  * @return Whether any invoice was affected
	  */
	def productDeliveryEnds_=(newProductDeliveryEnd: LocalDate)(implicit connection: Connection) = 
		putColumn(model.productDeliveryEndColumn, newProductDeliveryEnd)
	
	/**
	  * Updates the recipient company details ids of the targeted invoices
	  * @param newRecipientCompanyDetailsId A new recipient company details id to assign
	  * @return Whether any invoice was affected
	  */
	def recipientCompanyDetailsIds_=(newRecipientCompanyDetailsId: Int)(implicit connection: Connection) = 
		putColumn(model.recipientCompanyDetailsIdColumn, newRecipientCompanyDetailsId)
	
	/**
	  * Updates the reference codes of the targeted invoices
	  * @param newReferenceCode A new reference code to assign
	  * @return Whether any invoice was affected
	  */
	def referenceCodes_=(newReferenceCode: String)(implicit connection: Connection) = 
		putColumn(model.referenceCodeColumn, newReferenceCode)
	
	/**
	  * Updates the sender bank account ids of the targeted invoices
	  * @param newSenderBankAccountId A new sender bank account id to assign
	  * @return Whether any invoice was affected
	  */
	def senderBankAccountIds_=(newSenderBankAccountId: Int)(implicit connection: Connection) = 
		putColumn(model.senderBankAccountIdColumn, newSenderBankAccountId)
	
	/**
	  * Updates the sender company details ids of the targeted invoices
	  * @param newSenderCompanyDetailsId A new sender company details id to assign
	  * @return Whether any invoice was affected
	  */
	def senderCompanyDetailsIds_=(newSenderCompanyDetailsId: Int)(implicit connection: Connection) = 
		putColumn(model.senderCompanyDetailsIdColumn, newSenderCompanyDetailsId)
}

