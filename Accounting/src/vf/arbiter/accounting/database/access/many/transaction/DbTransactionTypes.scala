package vf.arbiter.accounting.database.access.many.transaction

import utopia.citadel.database.access.many.description.ManyDescribedAccessByIds
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.accounting.model.combined.transaction.DescribedTransactionType
import vf.arbiter.accounting.model.stored.transaction.TransactionType

/**
  * The root access point when targeting multiple transaction types at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbTransactionTypes extends ManyTransactionTypesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted transaction types
	  * @return An access point to transaction types with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbTransactionTypesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbTransactionTypesSubset(override val ids: Set[Int]) 
		extends ManyTransactionTypesAccess 
			with ManyDescribedAccessByIds[TransactionType, DescribedTransactionType]
}

