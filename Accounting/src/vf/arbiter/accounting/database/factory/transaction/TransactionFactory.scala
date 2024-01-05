package vf.arbiter.accounting.database.factory.transaction

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.accounting.database.ArbiterAccountingTables
import vf.arbiter.accounting.database.model.transaction.TransactionModel
import vf.arbiter.accounting.model.partial.transaction.TransactionData
import vf.arbiter.accounting.model.stored.transaction.Transaction

/**
  * Used for reading transaction data from the DB
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object TransactionFactory extends FromValidatedRowModelFactory[Transaction] with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def nonDeprecatedCondition = TransactionModel.nonDeprecatedCondition
	
	override def table = ArbiterAccountingTables.transaction
	
	override protected def fromValidatedModel(valid: Model) = 
		Transaction(valid("id").getInt, TransactionData(valid("accountId").getInt, 
			valid("date").getLocalDate, valid("recordDate").getLocalDate, valid("amount").getDouble, 
			valid("otherPartyEntryId").getInt, valid("referenceCode").getString, valid("creatorId").int, 
			valid("created").getInstant, valid("deprecatedAfter").instant))
}

