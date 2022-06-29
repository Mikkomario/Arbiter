package vf.arbiter.core.database.model.invoice

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Days
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import utopia.vault.nosql.storable.deprecation.NullDeprecatable
import vf.arbiter.core.database.factory.invoice.InvoiceFactory
import vf.arbiter.core.model.partial.invoice.InvoiceData
import vf.arbiter.core.model.stored.invoice.Invoice

import java.time.{Instant, LocalDate}

/**
  * Used for constructing InvoiceModel instances and for inserting invoices to the database
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
object InvoiceModel 
	extends DataInserter[InvoiceModel, Invoice, InvoiceData] with NullDeprecatable[InvoiceModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains invoice sender company details id
	  */
	val senderCompanyDetailsIdAttName = "senderCompanyDetailsId"
	
	/**
	  * Name of the property that contains invoice recipient company details id
	  */
	val recipientCompanyDetailsIdAttName = "recipientCompanyDetailsId"
	
	/**
	  * Name of the property that contains invoice sender bank account id
	  */
	val senderBankAccountIdAttName = "senderBankAccountId"
	
	/**
	  * Name of the property that contains invoice language id
	  */
	val languageIdAttName = "languageId"
	
	/**
	  * Name of the property that contains invoice reference code
	  */
	val referenceCodeAttName = "referenceCode"
	
	/**
	  * Name of the property that contains invoice payment duration
	  */
	val paymentDurationAttName = "paymentDurationDays"
	
	/**
	  * Name of the property that contains invoice product delivery begin
	  */
	val productDeliveryBeginAttName = "productDeliveryBegin"
	
	/**
	  * Name of the property that contains invoice product delivery end
	  */
	val productDeliveryEndAttName = "productDeliveryEnd"
	
	/**
	  * Name of the property that contains invoice creator id
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains invoice created
	  */
	val createdAttName = "created"
	
	/**
	  * Name of the property that contains invoice cancelled after
	  */
	val cancelledAfterAttName = "cancelledAfter"
	
	override val deprecationAttName = "cancelledAfter"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains invoice sender company details id
	  */
	def senderCompanyDetailsIdColumn = table(senderCompanyDetailsIdAttName)
	
	/**
	  * Column that contains invoice recipient company details id
	  */
	def recipientCompanyDetailsIdColumn = table(recipientCompanyDetailsIdAttName)
	
	/**
	  * Column that contains invoice sender bank account id
	  */
	def senderBankAccountIdColumn = table(senderBankAccountIdAttName)
	
	/**
	  * Column that contains invoice language id
	  */
	def languageIdColumn = table(languageIdAttName)
	
	/**
	  * Column that contains invoice reference code
	  */
	def referenceCodeColumn = table(referenceCodeAttName)
	
	/**
	  * Column that contains invoice payment duration
	  */
	def paymentDurationColumn = table(paymentDurationAttName)
	
	/**
	  * Column that contains invoice product delivery begin
	  */
	def productDeliveryBeginColumn = table(productDeliveryBeginAttName)
	
	/**
	  * Column that contains invoice product delivery end
	  */
	def productDeliveryEndColumn = table(productDeliveryEndAttName)
	
	/**
	  * Column that contains invoice creator id
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains invoice created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains invoice cancelled after
	  */
	def cancelledAfterColumn = table(cancelledAfterAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = InvoiceFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: InvoiceData) = 
		apply(None, Some(data.senderCompanyDetailsId), Some(data.recipientCompanyDetailsId), 
			Some(data.senderBankAccountId), Some(data.languageId), Some(data.referenceCode), 
			Some(data.paymentDuration), data.productDeliveryDates.flatMap { _.headOption },
			data.productDeliveryDates.flatMap { _.lastOption }, data.creatorId,
			Some(data.created), data.cancelledAfter)
	
	override def complete(id: Value, data: InvoiceData) = Invoice(id.getInt, data)
	
	override def withDeprecatedAfter(deprecationTime: Instant) = withCancelledAfter(deprecationTime)
	
	
	// OTHER	--------------------
	
	/**
	  * 
		@param cancelledAfter Time when this invoice became deprecated. None while this invoice is still valid.
	  * @return A model containing only the specified cancelled after
	  */
	def withCancelledAfter(cancelledAfter: Instant) = apply(cancelledAfter = Some(cancelledAfter))
	
	/**
	  * @param created Time when this invoice was first created
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who created this invoice
	  * @return A model containing only the specified creator id
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param id A invoice id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param languageId Id of the language used in this invoice
	  * @return A model containing only the specified language id
	  */
	def withLanguageId(languageId: Int) = apply(languageId = Some(languageId))
	
	/**
	  * 
		@param paymentDuration Number of days during which this invoice can be paid before additional consequences
	  * @return A model containing only the specified payment duration
	  */
	def withPaymentDuration(paymentDuration: Days) = apply(paymentDuration = Some(paymentDuration))
	
	/**
	  * @param productDeliveryBegin The first date when the products were delivered, if applicable
	  * @return A model containing only the specified product delivery begin
	  */
	def withProductDeliveryBegin(productDeliveryBegin: LocalDate) = 
		apply(productDeliveryBegin = Some(productDeliveryBegin))
	
	/**
	  * @param productDeliveryEnd The last date when the invoiced products were delivered, if applicable
	  * @return A model containing only the specified product delivery end
	  */
	def withProductDeliveryEnd(productDeliveryEnd: LocalDate) = 
		apply(productDeliveryEnd = Some(productDeliveryEnd))
	
	/**
	  * @param recipientCompanyDetailsId Id of the details of the recipient company used in this invoice
	  * @return A model containing only the specified recipient company details id
	  */
	def withRecipientCompanyDetailsId(recipientCompanyDetailsId: Int) = 
		apply(recipientCompanyDetailsId = Some(recipientCompanyDetailsId))
	
	/**
	  * @param referenceCode A custom reference code used by the sender to identify this invoice
	  * @return A model containing only the specified reference code
	  */
	def withReferenceCode(referenceCode: String) = apply(referenceCode = Some(referenceCode))
	
	/**
	  * 
		@param senderBankAccountId Id of the bank account the invoice sender wants the recipient to transfer money to
	  * @return A model containing only the specified sender bank account id
	  */
	def withSenderBankAccountId(senderBankAccountId: Int) = apply(senderBankAccountId = Some(senderBankAccountId))
	
	/**
	  * 
		@param senderCompanyDetailsId Id of the details of the company who sent this invoice (payment recipient)
	  * @return A model containing only the specified sender company details id
	  */
	def withSenderCompanyDetailsId(senderCompanyDetailsId: Int) = 
		apply(senderCompanyDetailsId = Some(senderCompanyDetailsId))
}

/**
  * Used for interacting with Invoices in the database
  * @param id invoice database id
  * @param senderCompanyDetailsId Id of the details of the company who sent this invoice (payment recipient)
  * @param recipientCompanyDetailsId Id of the details of the recipient company used in this invoice
  * 
	@param senderBankAccountId Id of the bank account the invoice sender wants the recipient to transfer money to
  * @param languageId Id of the language used in this invoice
  * @param referenceCode A custom reference code used by the sender to identify this invoice
  * @param paymentDuration Number of days during which this invoice can be paid before additional consequences
  * @param productDeliveryBegin The first date when the products were delivered, if applicable
  * @param productDeliveryEnd The last date when the invoiced products were delivered, if applicable
  * @param creatorId Id of the user who created this invoice
  * @param created Time when this invoice was first created
  * @param cancelledAfter Time when this invoice became deprecated. None while this invoice is still valid.
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
case class InvoiceModel(id: Option[Int] = None, senderCompanyDetailsId: Option[Int] = None, 
	recipientCompanyDetailsId: Option[Int] = None, senderBankAccountId: Option[Int] = None, 
	languageId: Option[Int] = None, referenceCode: Option[String] = None, 
	paymentDuration: Option[Days] = None, productDeliveryBegin: Option[LocalDate] = None, 
	productDeliveryEnd: Option[LocalDate] = None, creatorId: Option[Int] = None, 
	created: Option[Instant] = None, cancelledAfter: Option[Instant] = None) 
	extends StorableWithFactory[Invoice]
{
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceModel.factory
	
	override def valueProperties = {
		import InvoiceModel._
		Vector("id" -> id, senderCompanyDetailsIdAttName -> senderCompanyDetailsId, 
			recipientCompanyDetailsIdAttName -> recipientCompanyDetailsId, 
			senderBankAccountIdAttName -> senderBankAccountId, languageIdAttName -> languageId, 
			referenceCodeAttName -> referenceCode, 
			paymentDurationAttName -> paymentDuration.map { _.length }, 
			productDeliveryBeginAttName -> productDeliveryBegin, 
			productDeliveryEndAttName -> productDeliveryEnd, creatorIdAttName -> creatorId, 
			createdAttName -> created, cancelledAfterAttName -> cancelledAfter)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param cancelledAfter A new cancelled after
	  * @return A new copy of this model with the specified cancelled after
	  */
	def withCancelledAfter(cancelledAfter: Instant) = copy(cancelledAfter = Some(cancelledAfter))
	
	/**
	  * @param created A new created
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param creatorId A new creator id
	  * @return A new copy of this model with the specified creator id
	  */
	def withCreatorId(creatorId: Int) = copy(creatorId = Some(creatorId))
	
	/**
	  * @param languageId A new language id
	  * @return A new copy of this model with the specified language id
	  */
	def withLanguageId(languageId: Int) = copy(languageId = Some(languageId))
	
	/**
	  * @param paymentDuration A new payment duration
	  * @return A new copy of this model with the specified payment duration
	  */
	def withPaymentDuration(paymentDuration: Days) = copy(paymentDuration = Some(paymentDuration))
	
	/**
	  * @param productDeliveryBegin A new product delivery begin
	  * @return A new copy of this model with the specified product delivery begin
	  */
	def withProductDeliveryBegin(productDeliveryBegin: LocalDate) = 
		copy(productDeliveryBegin = Some(productDeliveryBegin))
	
	/**
	  * @param productDeliveryEnd A new product delivery end
	  * @return A new copy of this model with the specified product delivery end
	  */
	def withProductDeliveryEnd(productDeliveryEnd: LocalDate) = 
		copy(productDeliveryEnd = Some(productDeliveryEnd))
	
	/**
	  * @param recipientCompanyDetailsId A new recipient company details id
	  * @return A new copy of this model with the specified recipient company details id
	  */
	def withRecipientCompanyDetailsId(recipientCompanyDetailsId: Int) = 
		copy(recipientCompanyDetailsId = Some(recipientCompanyDetailsId))
	
	/**
	  * @param referenceCode A new reference code
	  * @return A new copy of this model with the specified reference code
	  */
	def withReferenceCode(referenceCode: String) = copy(referenceCode = Some(referenceCode))
	
	/**
	  * @param senderBankAccountId A new sender bank account id
	  * @return A new copy of this model with the specified sender bank account id
	  */
	def withSenderBankAccountId(senderBankAccountId: Int) = copy(senderBankAccountId = Some(senderBankAccountId))
	
	/**
	  * @param senderCompanyDetailsId A new sender company details id
	  * @return A new copy of this model with the specified sender company details id
	  */
	def withSenderCompanyDetailsId(senderCompanyDetailsId: Int) = 
		copy(senderCompanyDetailsId = Some(senderCompanyDetailsId))
}

