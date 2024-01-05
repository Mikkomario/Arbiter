package vf.arbiter.accounting.database.access.single.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.accounting.database.model.transaction.TransactionModel

import java.time.{Instant, LocalDate}

/**
  * A common trait for access points which target individual transactions or similar items at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait UniqueTransactionAccessLike[+A] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the account on which this transaction was made. None if no transaction (or value) was found.
	  */
	def accountId(implicit connection: Connection) = pullColumn(model.accountIdColumn).int
	
	/**
	  * 
		Date when this transaction occurred (e.g. date of purchase). None if no transaction (or value) was found.
	  */
	def date(implicit connection: Connection) = pullColumn(model.dateColumn).localDate
	
	/**
	  * Date when this transaction was recorded / 
	  * actuated on the bank account.. None if no transaction (or value) was found.
	  */
	def recordDate(implicit connection: Connection) = pullColumn(model.recordDateColumn).localDate
	
	/**
	  * 
		The size of the transaction in €. Positive amounts indicate balance added to the account. Negative values
	  *  indicate withdrawals or purchases.. None if no transaction (or value) was found.
	  */
	def amount(implicit connection: Connection) = pullColumn(model.amountColumn).double
	
	/**
	  * Id of the other party of this transaction, 
	  * as it appears on this original statement.. None if no transaction (or value) was found.
	  */
	def otherPartyEntryId(implicit connection: Connection) = pullColumn(model.otherPartyEntryIdColumn).int
	
	/**
	  * 
		Reference code mentioned on the transaction. May be empty.. None if no transaction (or value) was found.
	  */
	def referenceCode(implicit connection: Connection) = pullColumn(model.referenceCodeColumn).getString
	
	/**
	  * 
		Id of the user who added this entry. None if unknown or if not applicable.. None if no transaction (or value)
	  *  was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this transaction was added to the database. None if no transaction (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Time when this entry was cancelled / 
		removed. None while valid.. None if no transaction (or value) was found.
	  */
	def deprecatedAfter(implicit connection: Connection) = pullColumn(model.deprecatedAfterColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = TransactionModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the account ids of the targeted transactions
	  * @param newAccountId A new account id to assign
	  * @return Whether any transaction was affected
	  */
	def accountId_=(newAccountId: Int)(implicit connection: Connection) = 
		putColumn(model.accountIdColumn, newAccountId)
	
	/**
	  * Updates the amounts of the targeted transactions
	  * @param newAmount A new amount to assign
	  * @return Whether any transaction was affected
	  */
	def amount_=(newAmount: Double)(implicit connection: Connection) = putColumn(model.amountColumn, 
		newAmount)
	
	/**
	  * Updates the creation times of the targeted transactions
	  * @param newCreated A new created to assign
	  * @return Whether any transaction was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted transactions
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any transaction was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the dates of the targeted transactions
	  * @param newDate A new date to assign
	  * @return Whether any transaction was affected
	  */
	def date_=(newDate: LocalDate)(implicit connection: Connection) = putColumn(model.dateColumn, newDate)
	
	/**
	  * Updates the deprecation times of the targeted transactions
	  * @param newDeprecatedAfter A new deprecated after to assign
	  * @return Whether any transaction was affected
	  */
	def deprecatedAfter_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.deprecatedAfterColumn, newDeprecatedAfter)
	
	/**
	  * Updates the other party entry ids of the targeted transactions
	  * @param newOtherPartyEntryId A new other party entry id to assign
	  * @return Whether any transaction was affected
	  */
	def otherPartyEntryId_=(newOtherPartyEntryId: Int)(implicit connection: Connection) = 
		putColumn(model.otherPartyEntryIdColumn, newOtherPartyEntryId)
	
	/**
	  * Updates the record dates of the targeted transactions
	  * @param newRecordDate A new record date to assign
	  * @return Whether any transaction was affected
	  */
	def recordDate_=(newRecordDate: LocalDate)(implicit connection: Connection) = 
		putColumn(model.recordDateColumn, newRecordDate)
	
	/**
	  * Updates the reference codes of the targeted transactions
	  * @param newReferenceCode A new reference code to assign
	  * @return Whether any transaction was affected
	  */
	def referenceCode_=(newReferenceCode: String)(implicit connection: Connection) = 
		putColumn(model.referenceCodeColumn, newReferenceCode)
}

