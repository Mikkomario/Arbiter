package vf.arbiter.core.database.model.invoice

import java.time.{Instant, LocalDate}
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Days
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.invoice.InvoiceFactory
import vf.arbiter.core.model.partial.invoice.InvoiceData
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * Used for constructing InvoiceModel instances and for inserting Invoices to the database
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object InvoiceModel extends DataInserter[InvoiceModel, Invoice, InvoiceData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains Invoice senderCompanyId
	  */
	val senderCompanyIdAttName = "senderCompanyId"
	
	/**
	  * Name of the property that contains Invoice recipientCompanyId
	  */
	val recipientCompanyIdAttName = "recipientCompanyId"
	
	/**
	  * Name of the property that contains Invoice referenceCode
	  */
	val referenceCodeAttName = "referenceCode"
	
	/**
	  * Name of the property that contains Invoice created
	  */
	val createdAttName = "created"
	
	/**
	  * Name of the property that contains Invoice paymentDurationDays
	  */
	val paymentDurationDaysAttName = "paymentDurationDays"
	
	/**
	  * Name of the property that contains Invoice productDeliveryDate
	  */
	val productDeliveryDateAttName = "productDeliveryDate"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains Invoice senderCompanyId
	  */
	def senderCompanyIdColumn = table(senderCompanyIdAttName)
	
	/**
	  * Column that contains Invoice recipientCompanyId
	  */
	def recipientCompanyIdColumn = table(recipientCompanyIdAttName)
	
	/**
	  * Column that contains Invoice referenceCode
	  */
	def referenceCodeColumn = table(referenceCodeAttName)
	
	/**
	  * Column that contains Invoice created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains Invoice paymentDurationDays
	  */
	def paymentDurationDaysColumn = table(paymentDurationDaysAttName)
	
	/**
	  * Column that contains Invoice productDeliveryDate
	  */
	def productDeliveryDateColumn = table(productDeliveryDateAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = InvoiceFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: InvoiceData) = 
		apply(None, Some(data.senderCompanyId), Some(data.recipientCompanyId), Some(data.referenceCode), 
			Some(data.created), Some(data.paymentDurationDays), data.productDeliveryDate)
	
	override def complete(id: Value, data: InvoiceData) = Invoice(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this Invoice was first created
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A Invoice id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param paymentDurationDays Number of days during which this invoice can be paid before additional consequences
	  * @return A model containing only the specified paymentDurationDays
	  */
	def withPaymentDurationDays(paymentDurationDays: Days) = 
		apply(paymentDurationDays = Some(paymentDurationDays))
	
	/**
	  * @param productDeliveryDate Date when the sold products were delivered, if applicable
	  * @return A model containing only the specified productDeliveryDate
	  */
	def withProductDeliveryDate(productDeliveryDate: LocalDate) = 
		apply(productDeliveryDate = Some(productDeliveryDate))
	
	/**
	  * @param recipientCompanyId Id of the recipient company of this invoice
	  * @return A model containing only the specified recipientCompanyId
	  */
	def withRecipientCompanyId(recipientCompanyId: Int) = apply(recipientCompanyId = Some(recipientCompanyId))
	
	/**
	  * @param referenceCode A custom reference code used by the sender to identify this invoice
	  * @return A model containing only the specified referenceCode
	  */
	def withReferenceCode(referenceCode: String) = apply(referenceCode = Some(referenceCode))
	
	/**
	  * @param senderCompanyId Id of the company who sent this invoice (payment recipient)
	  * @return A model containing only the specified senderCompanyId
	  */
	def withSenderCompanyId(senderCompanyId: Int) = apply(senderCompanyId = Some(senderCompanyId))
}

/**
  * Used for interacting with Invoices in the database
  * @param id Invoice database id
  * @param senderCompanyId Id of the company who sent this invoice (payment recipient)
  * @param recipientCompanyId Id of the recipient company of this invoice
  * @param referenceCode A custom reference code used by the sender to identify this invoice
  * @param created Time when this Invoice was first created
  * @param paymentDurationDays Number of days during which this invoice can be paid before additional consequences
  * @param productDeliveryDate Date when the sold products were delivered, if applicable
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class InvoiceModel(id: Option[Int] = None, senderCompanyId: Option[Int] = None, 
	recipientCompanyId: Option[Int] = None, referenceCode: Option[String] = None, 
	created: Option[Instant] = None, paymentDurationDays: Option[Days] = None, 
	productDeliveryDate: Option[LocalDate] = None) 
	extends StorableWithFactory[Invoice]
{
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceModel.factory
	
	override def valueProperties = 
	{
		import InvoiceModel._
		Vector("id" -> id, senderCompanyIdAttName -> senderCompanyId, 
			recipientCompanyIdAttName -> recipientCompanyId, referenceCodeAttName -> referenceCode, 
			createdAttName -> created, paymentDurationDaysAttName -> paymentDurationDays.map { _.length }, 
			productDeliveryDateAttName -> productDeliveryDate)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created A new created
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param paymentDurationDays A new paymentDurationDays
	  * @return A new copy of this model with the specified paymentDurationDays
	  */
	def withPaymentDurationDays(paymentDurationDays: Days) = copy(paymentDurationDays = Some(paymentDurationDays))
	
	/**
	  * @param productDeliveryDate A new productDeliveryDate
	  * @return A new copy of this model with the specified productDeliveryDate
	  */
	def withProductDeliveryDate(productDeliveryDate: LocalDate) = 
		copy(productDeliveryDate = Some(productDeliveryDate))
	
	/**
	  * @param recipientCompanyId A new recipientCompanyId
	  * @return A new copy of this model with the specified recipientCompanyId
	  */
	def withRecipientCompanyId(recipientCompanyId: Int) = copy(recipientCompanyId = Some(recipientCompanyId))
	
	/**
	  * @param referenceCode A new referenceCode
	  * @return A new copy of this model with the specified referenceCode
	  */
	def withReferenceCode(referenceCode: String) = copy(referenceCode = Some(referenceCode))
	
	/**
	  * @param senderCompanyId A new senderCompanyId
	  * @return A new copy of this model with the specified senderCompanyId
	  */
	def withSenderCompanyId(senderCompanyId: Int) = copy(senderCompanyId = Some(senderCompanyId))
}

