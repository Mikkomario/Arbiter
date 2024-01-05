package vf.arbiter.accounting.database.access.many.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{NonDeprecatedView, UnconditionalView}
import vf.arbiter.accounting.model.combined.transaction.EvaluatedTransaction

/**
  * The root access point when targeting multiple evaluated transactions at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbEvaluatedTransactions 
	extends ManyEvaluatedTransactionsAccess with NonDeprecatedView[EvaluatedTransaction]
{
	// COMPUTED	--------------------
	
	/**
	  * A copy of this access point that includes historical (i.e. deprecated) evaluated transactions
	  */
	def includingHistory = DbEvaluatedTransactionsIncludingHistory
	
	
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted evaluated transactions
	  * @return An access point to evaluated transactions with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbEvaluatedTransactionsSubset(ids)
	
	
	// NESTED	--------------------
	
	object DbEvaluatedTransactionsIncludingHistory extends ManyEvaluatedTransactionsAccess 
		with UnconditionalView
	
	class DbEvaluatedTransactionsSubset(targetIds: Set[Int]) extends ManyEvaluatedTransactionsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

