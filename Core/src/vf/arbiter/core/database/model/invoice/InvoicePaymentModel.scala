package vf.arbiter.core.database.model.invoice

import java.time.{Instant, LocalDate}
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.invoice.InvoicePaymentFactory
import vf.arbiter.core.model.partial.invoice.InvoicePaymentData
import vf.arbiter.core.model.stored.invoice.InvoicePayment

/**
  * Used for constructing InvoicePaymentModel instances and for inserting InvoicePayments to the database
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object InvoicePaymentModel extends DataInserter[InvoicePaymentModel, InvoicePayment, InvoicePaymentData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains InvoicePayment invoiceId
	  */
	val invoiceIdAttName = "invoiceId"
	
	/**
	  * Name of the property that contains InvoicePayment date
	  */
	val dateAttName = "date"
	
	/**
	  * Name of the property that contains InvoicePayment receivedAmount
	  */
	val receivedAmountAttName = "receivedAmount"
	
	/**
	  * Name of the property that contains InvoicePayment remarks
	  */
	val remarksAttName = "remarks"
	
	/**
	  * Name of the property that contains InvoicePayment created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains InvoicePayment invoiceId
	  */
	def invoiceIdColumn = table(invoiceIdAttName)
	
	/**
	  * Column that contains InvoicePayment date
	  */
	def dateColumn = table(dateAttName)
	
	/**
	  * Column that contains InvoicePayment receivedAmount
	  */
	def receivedAmountColumn = table(receivedAmountAttName)
	
	/**
	  * Column that contains InvoicePayment remarks
	  */
	def remarksColumn = table(remarksAttName)
	
	/**
	  * Column that contains InvoicePayment created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = InvoicePaymentFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: InvoicePaymentData) = 
		apply(None, Some(data.invoiceId), Some(data.date), Some(data.receivedAmount), data.remarks, 
			Some(data.created))
	
	override def complete(id: Value, data: InvoicePaymentData) = InvoicePayment(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this payment was registered
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param date Date when this payment was received
	  * @return A model containing only the specified date
	  */
	def withDate(date: LocalDate) = apply(date = Some(date))
	
	/**
	  * @param id A InvoicePayment id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param invoiceId Id of the invoice that was paid
	  * @return A model containing only the specified invoiceId
	  */
	def withInvoiceId(invoiceId: Int) = apply(invoiceId = Some(invoiceId))
	
	/**
	  * @param receivedAmount Received amount in €, including taxes
	  * @return A model containing only the specified receivedAmount
	  */
	def withReceivedAmount(receivedAmount: Double) = apply(receivedAmount = Some(receivedAmount))
	
	/**
	  * @param remarks Free remarks concerning this payment
	  * @return A model containing only the specified remarks
	  */
	def withRemarks(remarks: String) = apply(remarks = Some(remarks))
}

/**
  * Used for interacting with InvoicePayments in the database
  * @param id InvoicePayment database id
  * @param invoiceId Id of the invoice that was paid
  * @param date Date when this payment was received
  * @param receivedAmount Received amount in €, including taxes
  * @param remarks Free remarks concerning this payment
  * @param created Time when this payment was registered
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class InvoicePaymentModel(id: Option[Int] = None, invoiceId: Option[Int] = None, 
	date: Option[LocalDate] = None, receivedAmount: Option[Double] = None, remarks: Option[String] = None, 
	created: Option[Instant] = None) 
	extends StorableWithFactory[InvoicePayment]
{
	// IMPLEMENTED	--------------------
	
	override def factory = InvoicePaymentModel.factory
	
	override def valueProperties = 
	{
		import InvoicePaymentModel._
		Vector("id" -> id, invoiceIdAttName -> invoiceId, dateAttName -> date, 
			receivedAmountAttName -> receivedAmount, remarksAttName -> remarks, createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created A new created
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param date A new date
	  * @return A new copy of this model with the specified date
	  */
	def withDate(date: LocalDate) = copy(date = Some(date))
	
	/**
	  * @param invoiceId A new invoiceId
	  * @return A new copy of this model with the specified invoiceId
	  */
	def withInvoiceId(invoiceId: Int) = copy(invoiceId = Some(invoiceId))
	
	/**
	  * @param receivedAmount A new receivedAmount
	  * @return A new copy of this model with the specified receivedAmount
	  */
	def withReceivedAmount(receivedAmount: Double) = copy(receivedAmount = Some(receivedAmount))
	
	/**
	  * @param remarks A new remarks
	  * @return A new copy of this model with the specified remarks
	  */
	def withRemarks(remarks: String) = copy(remarks = Some(remarks))
}

