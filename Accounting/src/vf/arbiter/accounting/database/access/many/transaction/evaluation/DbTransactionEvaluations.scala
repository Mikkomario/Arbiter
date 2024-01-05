package vf.arbiter.accounting.database.access.many.transaction.evaluation

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{NonDeprecatedView, UnconditionalView}
import vf.arbiter.accounting.model.stored.transaction.TransactionEvaluation

/**
  * The root access point when targeting multiple transaction evaluations at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbTransactionEvaluations 
	extends ManyTransactionEvaluationsAccess with NonDeprecatedView[TransactionEvaluation]
{
	// COMPUTED	--------------------
	
	/**
	  * A copy of this access point that includes historical (i.e. deprecated) transaction evaluations
	  */
	def includingHistory = DbTransactionEvaluationsIncludingHistory
	
	
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted transaction evaluations
	  * @return An access point to transaction evaluations with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbTransactionEvaluationsSubset(ids)
	
	
	// NESTED	--------------------
	
	object DbTransactionEvaluationsIncludingHistory 
		extends ManyTransactionEvaluationsAccess with UnconditionalView
	
	class DbTransactionEvaluationsSubset(targetIds: Set[Int]) extends ManyTransactionEvaluationsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

