package vf.arbiter.accounting.database.access.many.transaction

import utopia.citadel.database.access.many.description.ManyDescribedAccessByIds
import utopia.vault.nosql.view.{NonDeprecatedView, UnconditionalView}
import vf.arbiter.accounting.model.combined.transaction.DescribedTransaction
import vf.arbiter.accounting.model.stored.transaction.Transaction

/**
  * The root access point when targeting multiple transactions at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbTransactions extends ManyTransactionsAccess with NonDeprecatedView[Transaction]
{
	// COMPUTED	--------------------
	
	/**
	  * A copy of this access point that includes historical (i.e. deprecated) transactions
	  */
	def includingHistory = DbTransactionsIncludingHistory
	
	
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted transactions
	  * @return An access point to transactions with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbTransactionsSubset(ids)
	
	
	// NESTED	--------------------
	
	object DbTransactionsIncludingHistory extends ManyTransactionsAccess with UnconditionalView
	
	class DbTransactionsSubset(override val ids: Set[Int]) 
		extends ManyTransactionsAccess with ManyDescribedAccessByIds[Transaction, DescribedTransaction]
}

