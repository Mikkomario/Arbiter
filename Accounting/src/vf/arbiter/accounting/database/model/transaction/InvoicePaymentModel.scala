package vf.arbiter.accounting.database.model.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.accounting.database.factory.transaction.InvoicePaymentFactory
import vf.arbiter.accounting.model.partial.transaction.InvoicePaymentData
import vf.arbiter.accounting.model.stored.transaction.InvoicePayment

import java.time.Instant

/**
  * Used for constructing InvoicePaymentModel instances and for inserting invoice payments to the database
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object InvoicePaymentModel extends DataInserter[InvoicePaymentModel, InvoicePayment, InvoicePaymentData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains invoice payment invoice id
	  */
	val invoiceIdAttName = "invoiceId"
	
	/**
	  * Name of the property that contains invoice payment transaction id
	  */
	val transactionIdAttName = "transactionId"
	
	/**
	  * Name of the property that contains invoice payment creator id
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains invoice payment created
	  */
	val createdAttName = "created"
	
	/**
	  * Name of the property that contains invoice payment manual
	  */
	val manualAttName = "manual"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains invoice payment invoice id
	  */
	def invoiceIdColumn = table(invoiceIdAttName)
	
	/**
	  * Column that contains invoice payment transaction id
	  */
	def transactionIdColumn = table(transactionIdAttName)
	
	/**
	  * Column that contains invoice payment creator id
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains invoice payment created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains invoice payment manual
	  */
	def manualColumn = table(manualAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = InvoicePaymentFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: InvoicePaymentData) = 
		apply(None, Some(data.invoiceId), Some(data.transactionId), data.creatorId, Some(data.created), 
			Some(data.manual))
	
	override protected def complete(id: Value, data: InvoicePaymentData) = InvoicePayment(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this link was registered
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who registered this connection
	  * @return A model containing only the specified creator id
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param id A invoice payment id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param invoiceId Id of the paid invoice
	  * @return A model containing only the specified invoice id
	  */
	def withInvoiceId(invoiceId: Int) = apply(invoiceId = Some(invoiceId))
	
	/**
	  * @param manual Whether this connection was made manually (true), 
	  * or whether it was determined by an automated algorithm (false)
	  * @return A model containing only the specified manual
	  */
	def withManual(manual: Boolean) = apply(manual = Some(manual))
	
	/**
	  * @param transactionId Id of the transaction that paid the linked invoice
	  * @return A model containing only the specified transaction id
	  */
	def withTransactionId(transactionId: Int) = apply(transactionId = Some(transactionId))
}

/**
  * Used for interacting with InvoicePayments in the database
  * @param id invoice payment database id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class InvoicePaymentModel(id: Option[Int] = None, invoiceId: Option[Int] = None, 
	transactionId: Option[Int] = None, creatorId: Option[Int] = None, created: Option[Instant] = None, 
	manual: Option[Boolean] = None) 
	extends StorableWithFactory[InvoicePayment]
{
	// IMPLEMENTED	--------------------
	
	override def factory = InvoicePaymentModel.factory
	
	override def valueProperties = {
		import InvoicePaymentModel._
		Vector("id" -> id, invoiceIdAttName -> invoiceId, transactionIdAttName -> transactionId, 
			creatorIdAttName -> creatorId, createdAttName -> created, manualAttName -> manual)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this link was registered
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who registered this connection
	  * @return A new copy of this model with the specified creator id
	  */
	def withCreatorId(creatorId: Int) = copy(creatorId = Some(creatorId))
	
	/**
	  * @param invoiceId Id of the paid invoice
	  * @return A new copy of this model with the specified invoice id
	  */
	def withInvoiceId(invoiceId: Int) = copy(invoiceId = Some(invoiceId))
	
	/**
	  * @param manual Whether this connection was made manually (true), 
	  * or whether it was determined by an automated algorithm (false)
	  * @return A new copy of this model with the specified manual
	  */
	def withManual(manual: Boolean) = copy(manual = Some(manual))
	
	/**
	  * @param transactionId Id of the transaction that paid the linked invoice
	  * @return A new copy of this model with the specified transaction id
	  */
	def withTransactionId(transactionId: Int) = copy(transactionId = Some(transactionId))
}

