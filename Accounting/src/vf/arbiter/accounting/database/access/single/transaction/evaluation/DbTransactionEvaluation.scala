package vf.arbiter.accounting.database.access.single.transaction.evaluation

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NonDeprecatedView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.TransactionEvaluationFactory
import vf.arbiter.accounting.database.model.transaction.TransactionEvaluationModel
import vf.arbiter.accounting.model.stored.transaction.TransactionEvaluation

/**
  * Used for accessing individual transaction evaluations
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbTransactionEvaluation 
	extends SingleRowModelAccess[TransactionEvaluation] with NonDeprecatedView[TransactionEvaluation] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = TransactionEvaluationModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TransactionEvaluationFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted transaction evaluation
	  * @return An access point to that transaction evaluation
	  */
	def apply(id: Int) = DbSingleTransactionEvaluation(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique transaction evaluations.
	  * @return An access point to the transaction evaluation that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = 
		UniqueTransactionEvaluationAccess(mergeCondition(condition))
}

