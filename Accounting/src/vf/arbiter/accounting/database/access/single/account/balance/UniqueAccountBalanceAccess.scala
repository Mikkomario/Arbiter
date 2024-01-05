package vf.arbiter.accounting.database.access.single.account.balance

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleChronoRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NullDeprecatableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.account.AccountBalanceFactory
import vf.arbiter.accounting.database.model.account.AccountBalanceModel
import vf.arbiter.accounting.model.stored.account.AccountBalance

import java.time.Instant

object UniqueAccountBalanceAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueAccountBalanceAccess = new _UniqueAccountBalanceAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueAccountBalanceAccess(condition: Condition) extends UniqueAccountBalanceAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct account balances.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait UniqueAccountBalanceAccess 
	extends SingleChronoRowModelAccess[AccountBalance, UniqueAccountBalanceAccess] 
		with NullDeprecatableView[UniqueAccountBalanceAccess] 
		with DistinctModelAccess[AccountBalance, Option[AccountBalance], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the described bank account. None if no account balance (or value) was found.
	  */
	def accountId(implicit connection: Connection) = pullColumn(model.accountIdColumn).int
	
	/**
	  * The amount of € on this account in this instance. None if no account balance (or value) was found.
	  */
	def balance(implicit connection: Connection) = pullColumn(model.balanceColumn).double
	
	/**
	  * Id of the user who provided this information. None if not known or if not applicable.. None if
	  *  no account balance (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this value was specified. Also represents the time when this value was accurate.. None if
	  *  no account balance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * 
		Time when this statement was cancelled. None while valid.. None if no account balance (or value) was found.
	  */
	def deprecatedAfter(implicit connection: Connection) = pullColumn(model.deprecatedAfterColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AccountBalanceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AccountBalanceFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueAccountBalanceAccess = 
		new UniqueAccountBalanceAccess._UniqueAccountBalanceAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the account ids of the targeted account balances
	  * @param newAccountId A new account id to assign
	  * @return Whether any account balance was affected
	  */
	def accountId_=(newAccountId: Int)(implicit connection: Connection) = 
		putColumn(model.accountIdColumn, newAccountId)
	
	/**
	  * Updates the balances of the targeted account balances
	  * @param newBalance A new balance to assign
	  * @return Whether any account balance was affected
	  */
	def balance_=(newBalance: Double)(implicit connection: Connection) = 
		putColumn(model.balanceColumn, newBalance)
	
	/**
	  * Updates the creation times of the targeted account balances
	  * @param newCreated A new created to assign
	  * @return Whether any account balance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted account balances
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any account balance was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the deprecation times of the targeted account balances
	  * @param newDeprecatedAfter A new deprecated after to assign
	  * @return Whether any account balance was affected
	  */
	def deprecatedAfter_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.deprecatedAfterColumn, newDeprecatedAfter)
}

