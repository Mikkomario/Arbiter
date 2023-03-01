package vf.arbiter.core.database.access.single.invoice

import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.time.{Days, Now}
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.invoice.InvoiceFactory
import vf.arbiter.core.database.model.invoice.InvoiceModel
import vf.arbiter.core.model.stored.invoice.Invoice

import java.time.{Instant, LocalDate}

/**
  * A common trait for access points that return individual and distinct invoices.
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
trait UniqueInvoiceAccess 
	extends SingleRowModelAccess[Invoice] with DistinctModelAccess[Invoice, Option[Invoice], Value] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * 
		Id of the details of the company who sent this invoice (payment recipient). None if no instance (or value)
	  *  was found.
	  */
	def senderCompanyDetailsId(implicit connection: Connection) = 
		pullColumn(model.senderCompanyDetailsIdColumn).int
	
	/**
	  * 
		Id of the details of the recipient company used in this invoice. None if no instance (or value) was found.
	  */
	def recipientCompanyDetailsId(implicit connection: Connection) = 
		pullColumn(model.recipientCompanyDetailsIdColumn).int
	
	/**
	  * Id of the bank account the invoice sender wants the recipient to transfer money to. None if
	  *  no instance (or value) was found.
	  */
	def senderBankAccountId(implicit connection: Connection) = pullColumn(model.senderBankAccountIdColumn).int
	
	/**
	  * Id of the language used in this invoice. None if no instance (or value) was found.
	  */
	def languageId(implicit connection: Connection) = pullColumn(model.languageIdColumn).int
	
	/**
	  * 
		A custom reference code used by the sender to identify this invoice. None if no instance (or value) was found.
	  */
	def referenceCode(implicit connection: Connection) = pullColumn(model.referenceCodeColumn).string
	
	/**
	  * Number of days during which this invoice can be paid before additional consequences. None if
	  *  no instance (or value) was found.
	  */
	def paymentDuration(implicit connection: Connection) = 
		pullColumn(model.paymentDurationColumn).int.map { Days(_) }
	
	/**
	  * The first date when the products were delivered, 
		if applicable. None if no instance (or value) was found.
	  */
	def productDeliveryBegin(implicit connection: Connection) = 
		pullColumn(model.productDeliveryBeginColumn).localDate
	
	/**
	  * The last date when the invoiced products were delivered, 
	  * if applicable. None if no instance (or value) was found.
	  */
	def productDeliveryEnd(implicit connection: Connection) = pullColumn(model
		.productDeliveryEndColumn).localDate
	
	/**
	  * Id of the user who created this invoice. None if no instance (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this invoice was first created. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Time when this invoice became deprecated. None while this invoice is still valid.. None if
	  *  no instance (or value) was found.
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
	  * Updates the cancelled afters of the targeted invoices
	  * @param newCancelledAfter A new cancelled after to assign
	  * @return Whether any invoice was affected
	  */
	def cancelledAfter_=(newCancelledAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.cancelledAfterColumn, newCancelledAfter)
	
	/**
	  * Updates the creation times of the targeted invoices
	  * @param newCreated A new created to assign
	  * @return Whether any invoice was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted invoices
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any invoice was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Deprecates all accessible invoices
	  * @return Whether any row was targeted
	  */
	def deprecate()(implicit connection: Connection) = cancelledAfter = Now
	
	/**
	  * Updates the languages ids of the targeted invoices
	  * @param newLanguageId A new language id to assign
	  * @return Whether any invoice was affected
	  */
	def languageId_=(newLanguageId: Int)(implicit connection: Connection) = 
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
	def productDeliveryBegin_=(newProductDeliveryBegin: LocalDate)(implicit connection: Connection) = 
		putColumn(model.productDeliveryBeginColumn, newProductDeliveryBegin)
	
	/**
	  * Updates the product delivery ends of the targeted invoices
	  * @param newProductDeliveryEnd A new product delivery end to assign
	  * @return Whether any invoice was affected
	  */
	def productDeliveryEnd_=(newProductDeliveryEnd: LocalDate)(implicit connection: Connection) = 
		putColumn(model.productDeliveryEndColumn, newProductDeliveryEnd)
	
	/**
	  * Updates the recipient company details ids of the targeted invoices
	  * @param newRecipientCompanyDetailsId A new recipient company details id to assign
	  * @return Whether any invoice was affected
	  */
	def recipientCompanyDetailsId_=(newRecipientCompanyDetailsId: Int)(implicit connection: Connection) = 
		putColumn(model.recipientCompanyDetailsIdColumn, newRecipientCompanyDetailsId)
	
	/**
	  * Updates the reference codes of the targeted invoices
	  * @param newReferenceCode A new reference code to assign
	  * @return Whether any invoice was affected
	  */
	def referenceCode_=(newReferenceCode: String)(implicit connection: Connection) = 
		putColumn(model.referenceCodeColumn, newReferenceCode)
	
	/**
	  * Updates the sender bank account ids of the targeted invoices
	  * @param newSenderBankAccountId A new sender bank account id to assign
	  * @return Whether any invoice was affected
	  */
	def senderBankAccountId_=(newSenderBankAccountId: Int)(implicit connection: Connection) = 
		putColumn(model.senderBankAccountIdColumn, newSenderBankAccountId)
	
	/**
	  * Updates the sender company details ids of the targeted invoices
	  * @param newSenderCompanyDetailsId A new sender company details id to assign
	  * @return Whether any invoice was affected
	  */
	def senderCompanyDetailsId_=(newSenderCompanyDetailsId: Int)(implicit connection: Connection) = 
		putColumn(model.senderCompanyDetailsIdColumn, newSenderCompanyDetailsId)
}

