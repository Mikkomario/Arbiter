package vf.arbiter.core.database.access.many.invoice

import java.time.{Instant, LocalDate}
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Days
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.invoice.InvoiceFactory
import vf.arbiter.core.database.model.invoice.InvoiceModel
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
trait ManyInvoicesAccess extends ManyRowModelAccess[Invoice] with Indexed
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
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceFactory
	
	override protected def defaultOrdering = Some(factory.defaultOrdering)
	
	override def filter(additionalCondition: Condition): ManyInvoicesAccess =
		new ManyInvoicesAccess.ManyInvoicesSubView(this, additionalCondition)
	
	
	// OTHER	--------------------
	
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

