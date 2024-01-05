package vf.arbiter.accounting.database.factory.transaction

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.accounting.model.combined.transaction.EvaluatedTransaction
import vf.arbiter.accounting.model.stored.transaction.{Transaction, TransactionEvaluation}

/**
  * Used for reading evaluated transactions from the database
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object EvaluatedTransactionFactory 
	extends CombiningFactory[EvaluatedTransaction, Transaction, TransactionEvaluation] with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = TransactionEvaluationFactory
	
	override def nonDeprecatedCondition = 
		parentFactory.nonDeprecatedCondition && childFactory.nonDeprecatedCondition
	
	override def parentFactory = TransactionFactory
	
	override def apply(transaction: Transaction, evaluation: TransactionEvaluation) = 
		EvaluatedTransaction(transaction, evaluation)
}

