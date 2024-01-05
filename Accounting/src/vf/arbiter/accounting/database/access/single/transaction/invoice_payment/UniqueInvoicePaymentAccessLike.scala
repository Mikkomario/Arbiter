package vf.arbiter.accounting.database.access.single.transaction.invoice_payment

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.accounting.database.model.transaction.InvoicePaymentModel

import java.time.Instant

/**
  * A common trait for access points which target individual invoice payments or similar items at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait UniqueInvoicePaymentAccessLike[+A] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the paid invoice. None if no invoice payment (or value) was found.
	  */
	def invoiceId(implicit connection: Connection) = pullColumn(model.invoiceIdColumn).int
	
	/**
	  * Id of the transaction that paid the linked invoice. None if no invoice payment (or value) was found.
	  */
	def transactionId(implicit connection: Connection) = pullColumn(model.transactionIdColumn).int
	
	/**
	  * Id of the user who registered this connection. None if no invoice payment (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this link was registered. None if no invoice payment (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Whether this connection was made manually (true), 
	  * 
		or whether it was determined by an automated algorithm (false). None if no invoice payment (or value) was found.
	  */
	def manual(implicit connection: Connection) = pullColumn(model.manualColumn).boolean
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoicePaymentModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted invoice payments
	  * @param newCreated A new created to assign
	  * @return Whether any invoice payment was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted invoice payments
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any invoice payment was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the invoice ids of the targeted invoice payments
	  * @param newInvoiceId A new invoice id to assign
	  * @return Whether any invoice payment was affected
	  */
	def invoiceId_=(newInvoiceId: Int)(implicit connection: Connection) = 
		putColumn(model.invoiceIdColumn, newInvoiceId)
	
	/**
	  * Updates the are manual of the targeted invoice payments
	  * @param newManual A new manual to assign
	  * @return Whether any invoice payment was affected
	  */
	def manual_=(newManual: Boolean)(implicit connection: Connection) = putColumn(model.manualColumn, 
		newManual)
	
	/**
	  * Updates the transaction ids of the targeted invoice payments
	  * @param newTransactionId A new transaction id to assign
	  * @return Whether any invoice payment was affected
	  */
	def transactionId_=(newTransactionId: Int)(implicit connection: Connection) = 
		putColumn(model.transactionIdColumn, newTransactionId)
}

