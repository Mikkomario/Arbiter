package vf.arbiter.accounting.database.model.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import utopia.vault.nosql.storable.deprecation.DeprecatableAfter
import vf.arbiter.accounting.database.factory.transaction.TransactionFactory
import vf.arbiter.accounting.model.partial.transaction.TransactionData
import vf.arbiter.accounting.model.stored.transaction.Transaction

import java.time.{Instant, LocalDate}

/**
  * Used for constructing TransactionModel instances and for inserting transactions to the database
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object TransactionModel 
	extends DataInserter[TransactionModel, Transaction, TransactionData] 
		with DeprecatableAfter[TransactionModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains transaction account id
	  */
	val accountIdAttName = "accountId"
	
	/**
	  * Name of the property that contains transaction date
	  */
	val dateAttName = "date"
	
	/**
	  * Name of the property that contains transaction record date
	  */
	val recordDateAttName = "recordDate"
	
	/**
	  * Name of the property that contains transaction amount
	  */
	val amountAttName = "amount"
	
	/**
	  * Name of the property that contains transaction other party entry id
	  */
	val otherPartyEntryIdAttName = "otherPartyEntryId"
	
	/**
	  * Name of the property that contains transaction reference code
	  */
	val referenceCodeAttName = "referenceCode"
	
	/**
	  * Name of the property that contains transaction creator id
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains transaction created
	  */
	val createdAttName = "created"
	
	/**
	  * Name of the property that contains transaction deprecated after
	  */
	val deprecatedAfterAttName = "deprecatedAfter"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains transaction account id
	  */
	def accountIdColumn = table(accountIdAttName)
	
	/**
	  * Column that contains transaction date
	  */
	def dateColumn = table(dateAttName)
	
	/**
	  * Column that contains transaction record date
	  */
	def recordDateColumn = table(recordDateAttName)
	
	/**
	  * Column that contains transaction amount
	  */
	def amountColumn = table(amountAttName)
	
	/**
	  * Column that contains transaction other party entry id
	  */
	def otherPartyEntryIdColumn = table(otherPartyEntryIdAttName)
	
	/**
	  * Column that contains transaction reference code
	  */
	def referenceCodeColumn = table(referenceCodeAttName)
	
	/**
	  * Column that contains transaction creator id
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains transaction created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains transaction deprecated after
	  */
	def deprecatedAfterColumn = table(deprecatedAfterAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = TransactionFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: TransactionData) = 
		apply(None, Some(data.accountId), Some(data.date), Some(data.recordDate), Some(data.amount), 
			Some(data.otherPartyEntryId), data.referenceCode, data.creatorId, Some(data.created), 
			data.deprecatedAfter)
	
	override protected def complete(id: Value, data: TransactionData) = Transaction(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param accountId Id of the account on which this transaction was made
	  * @return A model containing only the specified account id
	  */
	def withAccountId(accountId: Int) = apply(accountId = Some(accountId))
	
	/**
	  * 
		@param amount The size of the transaction in €. Positive amounts indicate balance added to the account.
	  *  Negative values indicate withdrawals or purchases.
	  * @return A model containing only the specified amount
	  */
	def withAmount(amount: Double) = apply(amount = Some(amount))
	
	/**
	  * @param created Time when this transaction was added to the database
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who added this entry. None if unknown or if not applicable.
	  * @return A model containing only the specified creator id
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param date Date when this transaction occurred (e.g. date of purchase)
	  * @return A model containing only the specified date
	  */
	def withDate(date: LocalDate) = apply(date = Some(date))
	
	/**
	  * @param deprecatedAfter Time when this entry was cancelled / removed. None while valid.
	  * @return A model containing only the specified deprecated after
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = Some(deprecatedAfter))
	
	/**
	  * @param id A transaction id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param otherPartyEntryId Id of the other party of this transaction, 
		as it appears on this original statement.
	  * @return A model containing only the specified other party entry id
	  */
	def withOtherPartyEntryId(otherPartyEntryId: Int) = apply(otherPartyEntryId = Some(otherPartyEntryId))
	
	/**
	  * @param recordDate Date when this transaction was recorded / actuated on the bank account.
	  * @return A model containing only the specified record date
	  */
	def withRecordDate(recordDate: LocalDate) = apply(recordDate = Some(recordDate))
	
	/**
	  * @param referenceCode Reference code mentioned on the transaction. May be empty.
	  * @return A model containing only the specified reference code
	  */
	def withReferenceCode(referenceCode: String) = apply(referenceCode = referenceCode)
}

/**
  * Used for interacting with Transactions in the database
  * @param id transaction database id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class TransactionModel(id: Option[Int] = None, accountId: Option[Int] = None, 
	date: Option[LocalDate] = None, recordDate: Option[LocalDate] = None, amount: Option[Double] = None, 
	otherPartyEntryId: Option[Int] = None, referenceCode: String = "", creatorId: Option[Int] = None, 
	created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None) 
	extends StorableWithFactory[Transaction]
{
	// IMPLEMENTED	--------------------
	
	override def factory = TransactionModel.factory
	
	override def valueProperties = {
		import TransactionModel._
		Vector("id" -> id, accountIdAttName -> accountId, dateAttName -> date, 
			recordDateAttName -> recordDate, amountAttName -> amount, 
			otherPartyEntryIdAttName -> otherPartyEntryId, referenceCodeAttName -> referenceCode, 
			creatorIdAttName -> creatorId, createdAttName -> created, 
			deprecatedAfterAttName -> deprecatedAfter)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param accountId Id of the account on which this transaction was made
	  * @return A new copy of this model with the specified account id
	  */
	def withAccountId(accountId: Int) = copy(accountId = Some(accountId))
	
	/**
	  * 
		@param amount The size of the transaction in €. Positive amounts indicate balance added to the account.
	  *  Negative values indicate withdrawals or purchases.
	  * @return A new copy of this model with the specified amount
	  */
	def withAmount(amount: Double) = copy(amount = Some(amount))
	
	/**
	  * @param created Time when this transaction was added to the database
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who added this entry. None if unknown or if not applicable.
	  * @return A new copy of this model with the specified creator id
	  */
	def withCreatorId(creatorId: Int) = copy(creatorId = Some(creatorId))
	
	/**
	  * @param date Date when this transaction occurred (e.g. date of purchase)
	  * @return A new copy of this model with the specified date
	  */
	def withDate(date: LocalDate) = copy(date = Some(date))
	
	/**
	  * @param deprecatedAfter Time when this entry was cancelled / removed. None while valid.
	  * @return A new copy of this model with the specified deprecated after
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	/**
	  * @param otherPartyEntryId Id of the other party of this transaction, 
		as it appears on this original statement.
	  * @return A new copy of this model with the specified other party entry id
	  */
	def withOtherPartyEntryId(otherPartyEntryId: Int) = copy(otherPartyEntryId = Some(otherPartyEntryId))
	
	/**
	  * @param recordDate Date when this transaction was recorded / actuated on the bank account.
	  * @return A new copy of this model with the specified record date
	  */
	def withRecordDate(recordDate: LocalDate) = copy(recordDate = Some(recordDate))
	
	/**
	  * @param referenceCode Reference code mentioned on the transaction. May be empty.
	  * @return A new copy of this model with the specified reference code
	  */
	def withReferenceCode(referenceCode: String) = copy(referenceCode = referenceCode)
}

