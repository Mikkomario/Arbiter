package vf.arbiter.accounting.model.stored.transaction

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.accounting.database.access.single.transaction.DbSingleTransaction
import vf.arbiter.accounting.model.partial.transaction.TransactionData

/**
  * Represents a transaction that has already been stored in the database
  * @param id id of this transaction in the database
  * @param data Wrapped transaction data
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class Transaction(id: Int, data: TransactionData) extends StoredModelConvertible[TransactionData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this transaction in the database
	  */
	def access = DbSingleTransaction(id)
}

