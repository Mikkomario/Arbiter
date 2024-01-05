package vf.arbiter.accounting.database.factory.transaction

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.accounting.database.ArbiterAccountingTables
import vf.arbiter.accounting.database.model.transaction.TransactionEvaluationModel
import vf.arbiter.accounting.model.partial.transaction.TransactionEvaluationData
import vf.arbiter.accounting.model.stored.transaction.TransactionEvaluation

/**
  * Used for reading transaction evaluation data from the DB
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object TransactionEvaluationFactory 
	extends FromValidatedRowModelFactory[TransactionEvaluation] with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def nonDeprecatedCondition = TransactionEvaluationModel.nonDeprecatedCondition
	
	override def table = ArbiterAccountingTables.transactionEvaluation
	
	override protected def fromValidatedModel(valid: Model) = 
		TransactionEvaluation(valid("id").getInt, TransactionEvaluationData(valid("transactionId").getInt, 
			valid("typeId").getInt, valid("vatRatio").getDouble, valid("otherPartyAlias").getString, 
			valid("creatorId").int, valid("created").getInstant, valid("deprecatedAfter").instant, 
			valid("manual").getBoolean))
}

