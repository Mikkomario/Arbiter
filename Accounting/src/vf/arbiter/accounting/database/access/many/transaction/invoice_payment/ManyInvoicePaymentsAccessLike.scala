package vf.arbiter.accounting.database.access.many.transaction.invoice_payment

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.arbiter.accounting.database.model.transaction.InvoicePaymentModel

import java.time.Instant

/**
  * A common trait for access points which target multiple invoice payments or similar instances at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait ManyInvoicePaymentsAccessLike[+A, +Repr] 
	extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * invoice ids of the accessible invoice payments
	  */
	def invoiceIds(implicit connection: Connection) = pullColumn(model.invoiceIdColumn).map { v => v.getInt }
	
	/**
	  * transaction ids of the accessible invoice payments
	  */
	def transactionIds(implicit connection: Connection) = 
		pullColumn(model.transactionIdColumn).map { v => v.getInt }
	
	/**
	  * creator ids of the accessible invoice payments
	  */
	def creatorIds(implicit connection: Connection) = pullColumn(model.creatorIdColumn).flatMap { v => v.int }
	
	/**
	  * creation times of the accessible invoice payments
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	/**
	  * are manual of the accessible invoice payments
	  */
	def areManual(implicit connection: Connection) = pullColumn(model.manualColumn).map { v => v.getBoolean }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoicePaymentModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the are manual of the targeted invoice payments
	  * @param newManual A new manual to assign
	  * @return Whether any invoice payment was affected
	  */
	def areManual_=(newManual: Boolean)(implicit connection: Connection) = 
		putColumn(model.manualColumn, newManual)
	
	/**
	  * Updates the creation times of the targeted invoice payments
	  * @param newCreated A new created to assign
	  * @return Whether any invoice payment was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted invoice payments
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any invoice payment was affected
	  */
	def creatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the invoice ids of the targeted invoice payments
	  * @param newInvoiceId A new invoice id to assign
	  * @return Whether any invoice payment was affected
	  */
	def invoiceIds_=(newInvoiceId: Int)(implicit connection: Connection) = 
		putColumn(model.invoiceIdColumn, newInvoiceId)
	
	/**
	  * Updates the transaction ids of the targeted invoice payments
	  * @param newTransactionId A new transaction id to assign
	  * @return Whether any invoice payment was affected
	  */
	def transactionIds_=(newTransactionId: Int)(implicit connection: Connection) = 
		putColumn(model.transactionIdColumn, newTransactionId)
}

