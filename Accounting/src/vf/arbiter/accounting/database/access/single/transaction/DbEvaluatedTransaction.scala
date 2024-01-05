package vf.arbiter.accounting.database.access.single.transaction

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NonDeprecatedView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.EvaluatedTransactionFactory
import vf.arbiter.accounting.database.model.transaction.{TransactionEvaluationModel, TransactionModel}
import vf.arbiter.accounting.model.combined.transaction.EvaluatedTransaction

/**
  * Used for accessing individual evaluated transactions
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbEvaluatedTransaction 
	extends SingleRowModelAccess[EvaluatedTransaction] with NonDeprecatedView[EvaluatedTransaction] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with linked transactions
	  */
	protected def model = TransactionModel
	
	/**
	  * A database model (factory) used for interacting with the linked evaluation
	  */
	protected def evaluationModel = TransactionEvaluationModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = EvaluatedTransactionFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted evaluated transaction
	  * @return An access point to that evaluated transaction
	  */
	def apply(id: Int) = DbSingleEvaluatedTransaction(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique evaluated transactions.
	  * @return An access point to the evaluated transaction that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = 
		UniqueEvaluatedTransactionAccess(mergeCondition(condition))
}

