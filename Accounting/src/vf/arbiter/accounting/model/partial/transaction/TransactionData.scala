package vf.arbiter.accounting.model.partial.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.DoubleType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.LocalDateType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.{Instant, LocalDate}

object TransactionData extends FromModelFactoryWithSchema[TransactionData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("accountId", IntType, Vector("account_id")), 
			PropertyDeclaration("date", LocalDateType, isOptional = true), PropertyDeclaration("recordDate", 
			LocalDateType, Vector("record_date"), isOptional = true), PropertyDeclaration("amount", 
			DoubleType), PropertyDeclaration("otherPartyEntryId", IntType, Vector("other_party_entry_id")), 
			PropertyDeclaration("referenceCode", StringType, Vector("reference_code"), isOptional = true), 
			PropertyDeclaration("creatorId", IntType, Vector("creator_id"), isOptional = true), 
			PropertyDeclaration("created", InstantType, isOptional = true), 
			PropertyDeclaration("deprecatedAfter", InstantType, Vector("deprecated_after"), 
			isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		TransactionData(valid("accountId").getInt, valid("date").getLocalDate, 
			valid("recordDate").getLocalDate, valid("amount").getDouble, valid("otherPartyEntryId").getInt, 
			valid("referenceCode").getString, valid("creatorId").int, valid("created").getInstant, 
			valid("deprecatedAfter").instant)
}

/**
  * Represents a transaction from or to a bank account. Only includes data from a bank statement.
  * @param accountId Id of the account on which this transaction was made
  * @param date Date when this transaction occurred (e.g. date of purchase)
  * @param recordDate Date when this transaction was recorded / actuated on the bank account.
  * @param amount The size of the transaction in €. Positive amounts indicate balance added to the account.
  *  Negative values indicate withdrawals or purchases.
  * @param otherPartyEntryId Id of the other party of this transaction, 
	as it appears on this original statement.
  * @param referenceCode Reference code mentioned on the transaction. May be empty.
  * @param creatorId Id of the user who added this entry. None if unknown or if not applicable.
  * @param created Time when this transaction was added to the database
  * @param deprecatedAfter Time when this entry was cancelled / removed. None while valid.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class TransactionData(accountId: Int, date: LocalDate, recordDate: LocalDate, amount: Double, 
	otherPartyEntryId: Int, referenceCode: String = "", creatorId: Option[Int] = None, 
	created: Instant = Now, deprecatedAfter: Option[Instant] = None) 
	extends ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this transaction has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	
	/**
	  * Whether this transaction is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("accountId" -> accountId, "date" -> date, "recordDate" -> recordDate, 
			"amount" -> amount, "otherPartyEntryId" -> otherPartyEntryId, "referenceCode" -> referenceCode, 
			"creatorId" -> creatorId, "created" -> created, "deprecatedAfter" -> deprecatedAfter))
}

