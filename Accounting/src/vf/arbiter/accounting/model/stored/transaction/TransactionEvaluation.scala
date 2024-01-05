package vf.arbiter.accounting.model.stored.transaction

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.accounting.database.access.single.transaction.evaluation.DbSingleTransactionEvaluation
import vf.arbiter.accounting.model.partial.transaction.TransactionEvaluationData

/**
  * Represents a transaction evaluation that has already been stored in the database
  * @param id id of this transaction evaluation in the database
  * @param data Wrapped transaction evaluation data
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class TransactionEvaluation(id: Int, data: TransactionEvaluationData) 
	extends StoredModelConvertible[TransactionEvaluationData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this transaction evaluation in the database
	  */
	def access = DbSingleTransactionEvaluation(id)
}

