package vf.arbiter.core.database.access.single.invoice

import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.invoice.InvoicePaymentFactory
import vf.arbiter.core.database.model.invoice.InvoicePaymentModel
import vf.arbiter.core.model.stored.invoice.InvoicePayment

import java.time.{Instant, LocalDate}

/**
  * A common trait for access points that return individual and distinct invoice payments.
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
trait UniqueInvoicePaymentAccess 
	extends SingleRowModelAccess[InvoicePayment] 
		with DistinctModelAccess[InvoicePayment, Option[InvoicePayment], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the invoice that was paid. None if no instance (or value) was found.
	  */
	def invoiceId(implicit connection: Connection) = pullColumn(model.invoiceIdColumn).int
	
	/**
	  * Date when this payment was received. None if no instance (or value) was found.
	  */
	def date(implicit connection: Connection) = pullColumn(model.dateColumn).localDate
	
	/**
	  * Received amount in €, including taxes. None if no instance (or value) was found.
	  */
	def receivedAmount(implicit connection: Connection) = pullColumn(model.receivedAmountColumn).double
	
	/**
	  * Free remarks concerning this payment. None if no instance (or value) was found.
	  */
	def remarks(implicit connection: Connection) = pullColumn(model.remarksColumn).string
	
	/**
	  * Time when this payment was registered. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoicePaymentModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoicePaymentFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted invoice payments
	  * @param newCreated A new created to assign
	  * @return Whether any invoice payment was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the dates of the targeted invoice payments
	  * @param newDate A new date to assign
	  * @return Whether any invoice payment was affected
	  */
	def date_=(newDate: LocalDate)(implicit connection: Connection) = putColumn(model.dateColumn, newDate)
	
	/**
	  * Updates the invoices ids of the targeted invoice payments
	  * @param newInvoiceId A new invoice id to assign
	  * @return Whether any invoice payment was affected
	  */
	def invoiceId_=(newInvoiceId: Int)(implicit connection: Connection) = 
		putColumn(model.invoiceIdColumn, newInvoiceId)
	
	/**
	  * Updates the received amounts of the targeted invoice payments
	  * @param newReceivedAmount A new received amount to assign
	  * @return Whether any invoice payment was affected
	  */
	def receivedAmount_=(newReceivedAmount: Double)(implicit connection: Connection) = 
		putColumn(model.receivedAmountColumn, newReceivedAmount)
	
	/**
	  * Updates the remarkss of the targeted invoice payments
	  * @param newRemarks A new remarks to assign
	  * @return Whether any invoice payment was affected
	  */
	def remarks_=(newRemarks: String)(implicit connection: Connection) = 
		putColumn(model.remarksColumn, newRemarks)
}

