package vf.arbiter.core.database.access.single.invoice

import java.time.{Instant, LocalDate}
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.{Days, Now}
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.invoice.InvoiceFactory
import vf.arbiter.core.database.model.invoice.InvoiceModel
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * A common trait for access points that return individual and distinct Invoices.
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait UniqueInvoiceAccess 
	extends SingleRowModelAccess[Invoice] with DistinctModelAccess[Invoice, Option[Invoice], Value] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the details of the company who sent this invoice (payment recipient). None if no instance (or value) was found.
	  */
	def senderCompanyDetailsId(implicit connection: Connection) = 
		pullColumn(model.senderCompanyDetailsIdColumn).int
	
	/**
	  * Id of the details of the recipient company used in this invoice. None if no instance (or value) was found.
	  */
	def recipientCompanyDetailsId(implicit connection: Connection) = 
		pullColumn(model.recipientCompanyDetailsIdColumn).int
	
	/**
	  * Id of the bank account the invoice sender wants the recipient to transfer money to. None if no instance (or value) was found.
	  */
	def senderBankAccountId(implicit connection: Connection) = pullColumn(model.senderBankAccountIdColumn).int
	
	/**
	  * Id of the language used in this invoice. None if no instance (or value) was found.
	  */
	def languageId(implicit connection: Connection) = pullColumn(model.languageIdColumn).int
	
	/**
	  * A custom reference code used by the sender to identify this invoice. None if no instance (or value) was found.
	  */
	def referenceCode(implicit connection: Connection) = pullColumn(model.referenceCodeColumn).string
	
	/**
	  * Number of days during which this invoice can be paid before additional consequences. None if no instance (or value) was found.
	  */
	def paymentDuration(implicit connection: Connection) = 
		pullColumn(model.paymentDurationColumn).int.map { Days(_) }
	
	/**
	  * Date when the sold products were delivered, if applicable. None if no instance (or value) was found.
	  */
	def productDeliveryDate(implicit connection: Connection) = 
		pullColumn(model.productDeliveryDateColumn).localDate
	
	/**
	  * Id of the user who created this invoice. None if no instance (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this Invoice was first created. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Time when this Invoice became deprecated. None while this Invoice is still valid.. None if no instance (or value) was found.
	  */
	def cancelledAfter(implicit connection: Connection) = pullColumn(model.cancelledAfterColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoiceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceFactory
	
	
	// OTHER	--------------------
	
	/**
	 * Cancels the accessed invoice
	 * @param connection Implicit DB Connection
	 * @return Whether any invoice was targeted
	 */
	def cancel()(implicit connection: Connection) = cancelledAfter = Now
	
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

