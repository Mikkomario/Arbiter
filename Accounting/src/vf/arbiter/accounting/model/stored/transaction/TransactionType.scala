package vf.arbiter.accounting.model.stored.transaction

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.accounting.database.access.single.transaction.DbSingleTransactionType
import vf.arbiter.accounting.model.partial.transaction.TransactionTypeData

/**
  * Represents a transaction type that has already been stored in the database
  * @param id id of this transaction type in the database
  * @param data Wrapped transaction type data
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class TransactionType(id: Int, data: TransactionTypeData) 
	extends StoredModelConvertible[TransactionTypeData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this transaction type in the database
	  */
	def access = DbSingleTransactionType(id)
}

