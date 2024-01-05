package vf.arbiter.accounting.database.access.single.transaction.evaluation

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.accounting.model.stored.transaction.TransactionEvaluation

/**
  * An access point to individual transaction evaluations, based on their id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class DbSingleTransactionEvaluation(id: Int) 
	extends UniqueTransactionEvaluationAccess with SingleIntIdModelAccess[TransactionEvaluation]

