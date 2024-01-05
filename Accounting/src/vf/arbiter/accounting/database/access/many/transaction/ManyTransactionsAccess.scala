package vf.arbiter.accounting.database.access.many.transaction

import utopia.citadel.database.access.many.description.ManyDescribedAccess
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.access.many.description.DbTransactionDescriptions
import vf.arbiter.accounting.database.factory.transaction.TransactionFactory
import vf.arbiter.accounting.model.combined.transaction.DescribedTransaction
import vf.arbiter.accounting.model.stored.transaction.Transaction

object ManyTransactionsAccess
{
	// NESTED	--------------------
	
	private class ManyTransactionsSubView(condition: Condition) extends ManyTransactionsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple transactions at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait ManyTransactionsAccess 
	extends ManyTransactionsAccessLike[Transaction, ManyTransactionsAccess] 
		with ManyRowModelAccess[Transaction] with ManyDescribedAccess[Transaction, DescribedTransaction]
{
	// IMPLEMENTED	--------------------
	
	override def factory = TransactionFactory
	
	override protected def describedFactory = DescribedTransaction
	
	override protected def manyDescriptionsAccess = DbTransactionDescriptions
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyTransactionsAccess = 
		new ManyTransactionsAccess.ManyTransactionsSubView(mergeCondition(filterCondition))
	
	override def idOf(item: Transaction) = item.id
}

