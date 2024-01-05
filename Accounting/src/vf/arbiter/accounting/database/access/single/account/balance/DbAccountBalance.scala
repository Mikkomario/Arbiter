package vf.arbiter.accounting.database.access.single.account.balance

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NonDeprecatedView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.account.AccountBalanceFactory
import vf.arbiter.accounting.database.model.account.AccountBalanceModel
import vf.arbiter.accounting.model.stored.account.AccountBalance

/**
  * Used for accessing individual account balances
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbAccountBalance 
	extends SingleRowModelAccess[AccountBalance] with NonDeprecatedView[AccountBalance] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AccountBalanceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AccountBalanceFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted account balance
	  * @return An access point to that account balance
	  */
	def apply(id: Int) = DbSingleAccountBalance(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique account balances.
	  * @return An access point to the account balance that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueAccountBalanceAccess(mergeCondition(condition))
}

