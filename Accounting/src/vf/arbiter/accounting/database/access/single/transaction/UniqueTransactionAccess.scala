package vf.arbiter.accounting.database.access.single.transaction

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.NullDeprecatableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.TransactionFactory
import vf.arbiter.accounting.model.stored.transaction.Transaction

object UniqueTransactionAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueTransactionAccess = new _UniqueTransactionAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueTransactionAccess(condition: Condition) extends UniqueTransactionAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct transactions.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait UniqueTransactionAccess 
	extends UniqueTransactionAccessLike[Transaction] with SingleRowModelAccess[Transaction] 
		with NullDeprecatableView[UniqueTransactionAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = TransactionFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueTransactionAccess = 
		new UniqueTransactionAccess._UniqueTransactionAccess(mergeCondition(filterCondition))
}

