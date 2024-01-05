package vf.arbiter.accounting.database.access.single.transaction

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.TransactionTypeFactory
import vf.arbiter.accounting.database.model.transaction.TransactionTypeModel
import vf.arbiter.accounting.model.stored.transaction.TransactionType

/**
  * Used for accessing individual transaction types
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbTransactionType extends SingleRowModelAccess[TransactionType] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = TransactionTypeModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TransactionTypeFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted transaction type
	  * @return An access point to that transaction type
	  */
	def apply(id: Int) = DbSingleTransactionType(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique transaction types.
	  * @return An access point to the transaction type that satisfies the specified condition
	  */
	protected
		 def filterDistinct(condition: Condition) = UniqueTransactionTypeAccess(mergeCondition(condition))
}

