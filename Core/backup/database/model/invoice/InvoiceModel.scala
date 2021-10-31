package vf.arbiter.core.database.model.invoice

import java.time.{Instant, LocalDate}
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Days
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import utopia.vault.nosql.storable.deprecation.NullDeprecatable
import vf.arbiter.core.database.factory.invoice.InvoiceFactory
import vf.arbiter.core.model.partial.invoice.InvoiceData
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * Used for constructing InvoiceModel instances and for inserting Invoices to the database
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object InvoiceModel 
	extends DataInserter[InvoiceModel, Invoice, InvoiceData] with NullDeprecatable[InvoiceModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains Invoice senderCompanyDetailsId
	  */
	val senderCompanyDetailsIdAttName = "senderCompanyDetailsId"
	/**
	  * Name of the property that contains Invoice recipientCompanyDetailsId
	  */
	val recipientCompanyDetailsIdAttName = "recipientCompanyDetailsId"
	/**
	  * Name of the property that contains Invoice senderBankAccountId
	  */
	val senderBankAccountIdAttName = "senderBankAccountId"
	/**
	  * Name of the property that contains Invoice languageId
	  */
	val languageIdAttName = "languageId"
	/**
	  * Name of the property that contains Invoice referenceCode
	  */
	val referenceCodeAttName = "referenceCode"
	/**
	  * Name of the property that contains Invoice paymentDuration
	  */
	val paymentDurationAttName = "paymentDurationDays"
	/**
	  * Name of the property that contains Invoice productDeliveryDate
	  */
	val productDeliveryDateAttName = "productDeliveryDate"
	/**
	  * Name of the property that contains Invoice creatorId
	  */
	val creatorIdAttName = "creatorId"
	/**
	  * Name of the property that contains Invoice created
	  */
	val createdAttName = "created"
	/**
	  * Name of the property that contains Invoice cancelledAfter
	  */
	val cancelledAfterAttName = "cancelledAfter"
	
	override val deprecationAttName = "cancelledAfter"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains Invoice senderCompanyDetailsId
	  */
	def senderCompanyDetailsIdColumn = table(senderCompanyDetailsIdAttName)
	
	/**
	  * Column that contains Invoice recipientCompanyDetailsId
	  */
	def recipientCompanyDetailsIdColumn = table(recipientCompanyDetailsIdAttName)
	
	/**
	  * Column that contains Invoice senderBankAccountId
	  */
	def senderBankAccountIdColumn = table(senderBankAccountIdAttName)
	
	/**
	  * Column that contains Invoice languageId
	  */
	def languageIdColumn = table(languageIdAttName)
	
	/**
	  * Column that contains Invoice referenceCode
	  */
	def referenceCodeColumn = table(referenceCodeAttName)
	
	/**
	  * Column that contains Invoice paymentDuration
	  */
	def paymentDurationColumn = table(paymentDurationAttName)
	
	/**
	  * Column that contains Invoice productDeliveryDate
	  */
	def productDeliveryDateColumn = table(productDeliveryDateAttName)
	
	/**
	  * Column that contains Invoice creatorId
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains Invoice created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains Invoice cancelledAfter
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
			Some(data.paymentDuration), data.productDeliveryDate, data.creatorId, Some(data.created), 
			data.cancelledAfter)
	
	override def complete(id: Value, data: InvoiceData) = Invoice(id.getInt, data)
	
	override def withDeprecatedAfter(deprecationTime: Instant) = withCancelledAfter(deprecationTime)
	
	
	// OTHER	--------------------
	
	/**
	  * @param cancelledAfter Time when this Invoice became deprecated. None while this Invoice is still valid.
	  * @return A model containing only the specified cancelledAfter
	  */
	def withCancelledAfter(cancelledAfter: Instant) = apply(cancelledAfter = Some(cancelledAfter))
	
	/**
	  * @param created Time when this Invoice was first created
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who created this invoice
	  * @return A model containing only the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param id A Invoice id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param languageId Id of the language used in this invoice
	  * @return A model containing only the specified languageId
	  */
	def withLanguageId(languageId: Int) = apply(languageId = Some(languageId))
	
	/**
	  * @param paymentDuration Number of days during which this invoice can be paid before additional consequences
	  * @return A model containing only the specified paymentDuration
	  */
	def withPaymentDuration(paymentDuration: Days) = apply(paymentDuration = Some(paymentDuration))
	
	/**
	  * @param productDeliveryDate Date when the sold products were delivered, if applicable
	  * @return A model containing only the specified productDeliveryDate
	  */
	def withProductDeliveryDate(productDeliveryDate: LocalDate) = 
		apply(productDeliveryDate = Some(productDeliveryDate))
	
	/**
	  * @param recipientCompanyDetailsId Id of the details of the recipient company used in this invoice
	  * @return A model containing only the specified recipientCompanyDetailsId
	  */
	def withRecipientCompanyDetailsId(recipientCompanyDetailsId: Int) = 
		apply(recipientCompanyDetailsId = Some(recipientCompanyDetailsId))
	
	/**
	  * @param referenceCode A custom reference code used by the sender to identify this invoice
	  * @return A model containing only the specified referenceCode
	  */
	def withReferenceCode(referenceCode: String) = apply(referenceCode = Some(referenceCode))
	
	/**
	  * @param senderBankAccountId Id of the bank account the invoice sender wants the recipient to transfer money to
	  * @return A model containing only the specified senderBankAccountId
	  */
	def withSenderBankAccountId(senderBankAccountId: Int) = apply(senderBankAccountId = Some(senderBankAccountId))
	
	/**
	  * @param senderCompanyDetailsId Id of the details of the company who sent this invoice (payment recipient)
	  * @return A model containing only the specified senderCompanyDetailsId
	  */
	def withSenderCompanyDetailsId(senderCompanyDetailsId: Int) = 
		apply(senderCompanyDetailsId = Some(senderCompanyDetailsId))
}

/**
  * Used for interacting with Invoices in the database
  * @param id Invoice database id
  * @param senderCompanyDetailsId Id of the details of the company who sent this invoice (payment recipient)
  * @param recipientCompanyDetailsId Id of the details of the recipient company used in this invoice
  * @param senderBankAccountId Id of the bank account the invoice sender wants the recipient to transfer money to
  * @param languageId Id of the language used in this invoice
  * @param referenceCode A custom reference code used by the sender to identify this invoice
  * @param paymentDuration Number of days during which this invoice can be paid before additional consequences
  * @param productDeliveryDate Date when the sold products were delivered, if applicable
  * @param creatorId Id of the user who created this invoice
  * @param created Time when this Invoice was first created
  * @param cancelledAfter Time when this Invoice became deprecated. None while this Invoice is still valid.
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class InvoiceModel(id: Option[Int] = None, senderCompanyDetailsId: Option[Int] = None, 
	recipientCompanyDetailsId: Option[Int] = None, senderBankAccountId: Option[Int] = None, 
	languageId: Option[Int] = None, referenceCode: Option[String] = None, 
	paymentDuration: Option[Days] = None, productDeliveryDate: Option[LocalDate] = None, 
	creatorId: Option[Int] = None, created: Option[Instant] = None, cancelledAfter: Option[Instant] = None) 
	extends StorableWithFactory[Invoice]
{
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceModel.factory
	
	override def valueProperties = 
	{
		import InvoiceModel._
		Vector("id" -> id, senderCompanyDetailsIdAttName -> senderCompanyDetailsId, 
			recipientCompanyDetailsIdAttName -> recipientCompanyDetailsId, 
			senderBankAccountIdAttName -> senderBankAccountId, languageIdAttName -> languageId, 
			referenceCodeAttName -> referenceCode, 
			paymentDurationAttName -> paymentDuration.map { _.length }, 
			productDeliveryDateAttName -> productDeliveryDate, creatorIdAttName -> creatorId, 
			createdAttName -> created, cancelledAfterAttName -> cancelledAfter)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param cancelledAfter A new cancelledAfter
	  * @return A new copy of this model with the specified cancelledAfter
	  */
	def withCancelledAfter(cancelledAfter: Instant) = copy(cancelledAfter = Some(cancelledAfter))
	
	/**
	  * @param created A new created
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param creatorId A new creatorId
	  * @return A new copy of this model with the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = copy(creatorId = Some(creatorId))
	
	/**
	  * @param languageId A new languageId
	  * @return A new copy of this model with the specified languageId
	  */
	def withLanguageId(languageId: Int) = copy(languageId = Some(languageId))
	
	/**
	  * @param paymentDuration A new paymentDuration
	  * @return A new copy of this model with the specified paymentDuration
	  */
	def withPaymentDuration(paymentDuration: Days) = copy(paymentDuration = Some(paymentDuration))
	
	/**
	  * @param productDeliveryDate A new productDeliveryDate
	  * @return A new copy of this model with the specified productDeliveryDate
	  */
	def withProductDeliveryDate(productDeliveryDate: LocalDate) = 
		copy(productDeliveryDate = Some(productDeliveryDate))
	
	/**
	  * @param recipientCompanyDetailsId A new recipientCompanyDetailsId
	  * @return A new copy of this model with the specified recipientCompanyDetailsId
	  */
	def withRecipientCompanyDetailsId(recipientCompanyDetailsId: Int) = 
		copy(recipientCompanyDetailsId = Some(recipientCompanyDetailsId))
	
	/**
	  * @param referenceCode A new referenceCode
	  * @return A new copy of this model with the specified referenceCode
	  */
	def withReferenceCode(referenceCode: String) = copy(referenceCode = Some(referenceCode))
	
	/**
	  * @param senderBankAccountId A new senderBankAccountId
	  * @return A new copy of this model with the specified senderBankAccountId
	  */
	def withSenderBankAccountId(senderBankAccountId: Int) = copy(senderBankAccountId = Some(senderBankAccountId))
	
	/**
	  * @param senderCompanyDetailsId A new senderCompanyDetailsId
	  * @return A new copy of this model with the specified senderCompanyDetailsId
	  */
	def withSenderCompanyDetailsId(senderCompanyDetailsId: Int) = 
		copy(senderCompanyDetailsId = Some(senderCompanyDetailsId))
}

