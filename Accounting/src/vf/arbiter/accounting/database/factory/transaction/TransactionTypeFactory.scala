package vf.arbiter.accounting.database.factory.transaction

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.accounting.database.ArbiterAccountingTables
import vf.arbiter.accounting.model.partial.transaction.TransactionTypeData
import vf.arbiter.accounting.model.stored.transaction.TransactionType

/**
  * Used for reading transaction type data from the DB
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object TransactionTypeFactory extends FromValidatedRowModelFactory[TransactionType]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = ArbiterAccountingTables.transactionType
	
	override protected def fromValidatedModel(valid: Model) = 
		TransactionType(valid("id").getInt, TransactionTypeData(valid("parentId").int, 
			valid("creatorId").int, valid("created").getInstant, valid("preApplied").getBoolean))
}

