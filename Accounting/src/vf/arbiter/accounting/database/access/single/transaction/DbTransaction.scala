package vf.arbiter.accounting.database.access.single.transaction

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NonDeprecatedView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.TransactionFactory
import vf.arbiter.accounting.database.model.transaction.TransactionModel
import vf.arbiter.accounting.model.stored.transaction.Transaction

/**
  * Used for accessing individual transactions
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbTransaction 
	extends SingleRowModelAccess[Transaction] with NonDeprecatedView[Transaction] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = TransactionModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TransactionFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted transaction
	  * @return An access point to that transaction
	  */
	def apply(id: Int) = DbSingleTransaction(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique transactions.
	  * @return An access point to the transaction that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueTransactionAccess(mergeCondition(condition))
}

