package vf.arbiter.accounting.database.access.many.account.balance

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.{NonDeprecatedView, UnconditionalView}
import vf.arbiter.accounting.model.stored.account.AccountBalance

/**
  * The root access point when targeting multiple account balances at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbAccountBalances extends ManyAccountBalancesAccess with NonDeprecatedView[AccountBalance]
{
	// COMPUTED	--------------------
	
	/**
	  * A copy of this access point that includes historical (i.e. deprecated) account balances
	  */
	def includingHistory = DbAccountBalancesIncludingHistory
	
	
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted account balances
	  * @return An access point to account balances with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbAccountBalancesSubset(ids)
	
	
	// NESTED	--------------------
	
	object DbAccountBalancesIncludingHistory extends ManyAccountBalancesAccess with UnconditionalView
	
	class DbAccountBalancesSubset(targetIds: Set[Int]) extends ManyAccountBalancesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

