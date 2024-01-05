package vf.arbiter.accounting.database.access.single.transaction

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.accounting.model.combined.transaction.EvaluatedTransaction

/**
  * An access point to individual evaluated transactions, based on their transaction id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class DbSingleEvaluatedTransaction(id: Int) 
	extends UniqueEvaluatedTransactionAccess with SingleIntIdModelAccess[EvaluatedTransaction]

