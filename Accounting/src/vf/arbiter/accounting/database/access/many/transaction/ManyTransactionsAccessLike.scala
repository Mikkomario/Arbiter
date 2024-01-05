package vf.arbiter.accounting.database.access.many.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NullDeprecatableView
import vf.arbiter.accounting.database.model.transaction.TransactionModel

import java.time.{Instant, LocalDate}

/**
  * A common trait for access points which target multiple transactions or similar instances at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait ManyTransactionsAccessLike[+A, +Repr] 
	extends ManyModelAccess[A] with Indexed with NullDeprecatableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * account ids of the accessible transactions
	  */
	def accountIds(implicit connection: Connection) = pullColumn(model.accountIdColumn).map { v => v.getInt }
	
	/**
	  * dates of the accessible transactions
	  */
	def dates(implicit connection: Connection) = pullColumn(model.dateColumn).map { v => v.getLocalDate }
	
	/**
	  * record dates of the accessible transactions
	  */
	def recordDates(implicit connection: Connection) = 
		pullColumn(model.recordDateColumn).map { v => v.getLocalDate }
	
	/**
	  * amounts of the accessible transactions
	  */
	def amounts(implicit connection: Connection) = pullColumn(model.amountColumn).map { v => v.getDouble }
	
	/**
	  * other party entry ids of the accessible transactions
	  */
	def otherPartyEntryIds(implicit connection: Connection) = 
		pullColumn(model.otherPartyEntryIdColumn).map { v => v.getInt }
	
	/**
	  * reference codes of the accessible transactions
	  */
	def referenceCodes(implicit connection: Connection) = 
		pullColumn(model.referenceCodeColumn).flatMap { _.string }
	
	/**
	  * creator ids of the accessible transactions
	  */
	def creatorIds(implicit connection: Connection) = pullColumn(model.creatorIdColumn).flatMap { v => v.int }
	
	/**
	  * creation times of the accessible transactions
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	/**
	  * deprecation times of the accessible transactions
	  */
	def deprecationTimes(implicit connection: Connection) = 
		pullColumn(model.deprecatedAfterColumn).flatMap { v => v.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
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
	def accountIds_=(newAccountId: Int)(implicit connection: Connection) = 
		putColumn(model.accountIdColumn, newAccountId)
	
	/**
	  * Updates the amounts of the targeted transactions
	  * @param newAmount A new amount to assign
	  * @return Whether any transaction was affected
	  */
	def amounts_=(newAmount: Double)(implicit connection: Connection) = putColumn(model.amountColumn, 
		newAmount)
	
	/**
	  * Updates the creation times of the targeted transactions
	  * @param newCreated A new created to assign
	  * @return Whether any transaction was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted transactions
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any transaction was affected
	  */
	def creatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the dates of the targeted transactions
	  * @param newDate A new date to assign
	  * @return Whether any transaction was affected
	  */
	def dates_=(newDate: LocalDate)(implicit connection: Connection) = putColumn(model.dateColumn, newDate)
	
	/**
	  * Updates the deprecation times of the targeted transactions
	  * @param newDeprecatedAfter A new deprecated after to assign
	  * @return Whether any transaction was affected
	  */
	def deprecationTimes_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.deprecatedAfterColumn, newDeprecatedAfter)
	
	/**
	  * Updates the other party entry ids of the targeted transactions
	  * @param newOtherPartyEntryId A new other party entry id to assign
	  * @return Whether any transaction was affected
	  */
	def otherPartyEntryIds_=(newOtherPartyEntryId: Int)(implicit connection: Connection) = 
		putColumn(model.otherPartyEntryIdColumn, newOtherPartyEntryId)
	
	/**
	  * Updates the record dates of the targeted transactions
	  * @param newRecordDate A new record date to assign
	  * @return Whether any transaction was affected
	  */
	def recordDates_=(newRecordDate: LocalDate)(implicit connection: Connection) = 
		putColumn(model.recordDateColumn, newRecordDate)
	
	/**
	  * Updates the reference codes of the targeted transactions
	  * @param newReferenceCode A new reference code to assign
	  * @return Whether any transaction was affected
	  */
	def referenceCodes_=(newReferenceCode: String)(implicit connection: Connection) = 
		putColumn(model.referenceCodeColumn, newReferenceCode)
}

