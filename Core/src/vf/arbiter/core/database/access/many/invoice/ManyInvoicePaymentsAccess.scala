package vf.arbiter.core.database.access.many.invoice

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.invoice.InvoicePaymentFactory
import vf.arbiter.core.database.model.invoice.InvoicePaymentModel
import vf.arbiter.core.model.stored.invoice.InvoicePayment

import java.time.{Instant, LocalDate}

object ManyInvoicePaymentsAccess extends ViewFactory[ManyInvoicePaymentsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyInvoicePaymentsAccess = 
		_ManyInvoicePaymentsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyInvoicePaymentsAccess(override val accessCondition: Option[Condition]) 
		extends ManyInvoicePaymentsAccess
}

/**
  * A common trait for access points which target multiple InvoicePayments at a time
  * @author Mikko Hilpinen
  * @since 31.10.2021
  */
trait ManyInvoicePaymentsAccess 
	extends ManyRowModelAccess[InvoicePayment] with Indexed with FilterableView[ManyInvoicePaymentsAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * invoiceIds of the accessible InvoicePayments
	  */
	def invoiceIds(implicit connection: Connection) = 
		pullColumn(model.invoiceIdColumn).flatMap { value => value.int }
	
	/**
	  * dates of the accessible InvoicePayments
	  */
	def dates(implicit connection: Connection) = pullColumn(model.dateColumn)
		.flatMap { value => value.localDate }
	
	/**
	  * receivedAmounts of the accessible InvoicePayments
	  */
	def receivedAmounts(implicit connection: Connection) = 
		pullColumn(model.receivedAmountColumn).flatMap { value => value.double }
	
	/**
	  * remarkss of the accessible InvoicePayments
	  */
	def remarkss(implicit connection: Connection) = 
		pullColumn(model.remarksColumn).flatMap { value => value.string }
	
	/**
	  * creationTimes of the accessible InvoicePayments
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoicePaymentModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoicePaymentFactory
	
	override def self = this
	
	override def apply(condition: Condition): ManyInvoicePaymentsAccess = ManyInvoicePaymentsAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the created of the targeted InvoicePayment instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any InvoicePayment instance was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the date of the targeted InvoicePayment instance(s)
	  * @param newDate A new date to assign
	  * @return Whether any InvoicePayment instance was affected
	  */
	def dates_=(newDate: LocalDate)(implicit connection: Connection) = putColumn(model.dateColumn, newDate)
	
	/**
	  * Updates the invoiceId of the targeted InvoicePayment instance(s)
	  * @param newInvoiceId A new invoiceId to assign
	  * @return Whether any InvoicePayment instance was affected
	  */
	def invoiceIds_=(newInvoiceId: Int)(implicit connection: Connection) = 
		putColumn(model.invoiceIdColumn, newInvoiceId)
	
	/**
	  * Updates the receivedAmount of the targeted InvoicePayment instance(s)
	  * @param newReceivedAmount A new receivedAmount to assign
	  * @return Whether any InvoicePayment instance was affected
	  */
	def receivedAmounts_=(newReceivedAmount: Double)(implicit connection: Connection) = 
		putColumn(model.receivedAmountColumn, newReceivedAmount)
	
	/**
	  * Updates the remarks of the targeted InvoicePayment instance(s)
	  * @param newRemarks A new remarks to assign
	  * @return Whether any InvoicePayment instance was affected
	  */
	def remarkss_=(newRemarks: String)(implicit connection: Connection) = 
		putColumn(model.remarksColumn, newRemarks)
}

