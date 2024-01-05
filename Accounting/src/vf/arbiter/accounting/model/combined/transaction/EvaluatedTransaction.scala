package vf.arbiter.accounting.model.combined.transaction

import utopia.flow.view.template.Extender
import vf.arbiter.accounting.model.partial.transaction.TransactionData
import vf.arbiter.accounting.model.stored.transaction.{Transaction, TransactionEvaluation}

/**
  * Combines a transaction with its evaluation. This represents a complete transaction.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class EvaluatedTransaction(transaction: Transaction, evaluation: TransactionEvaluation) 
	extends Extender[TransactionData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this transaction in the database
	  */
	def id = transaction.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = transaction.data
}

