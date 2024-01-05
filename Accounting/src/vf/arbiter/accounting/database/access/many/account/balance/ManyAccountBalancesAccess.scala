package vf.arbiter.accounting.database.access.many.account.balance

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{ChronoRowFactoryView, NullDeprecatableView}
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.account.AccountBalanceFactory
import vf.arbiter.accounting.database.model.account.AccountBalanceModel
import vf.arbiter.accounting.model.stored.account.AccountBalance

import java.time.Instant

object ManyAccountBalancesAccess
{
	// NESTED	--------------------
	
	private class ManyAccountBalancesSubView(condition: Condition) extends ManyAccountBalancesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple account balances at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait ManyAccountBalancesAccess 
	extends ManyRowModelAccess[AccountBalance] 
		with ChronoRowFactoryView[AccountBalance, ManyAccountBalancesAccess] 
		with NullDeprecatableView[ManyAccountBalancesAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * account ids of the accessible account balances
	  */
	def accountIds(implicit connection: Connection) = pullColumn(model.accountIdColumn).map { v => v.getInt }
	
	/**
	  * balances of the accessible account balances
	  */
	def balances(implicit connection: Connection) = pullColumn(model.balanceColumn).map { v => v.getDouble }
	
	/**
	  * creator ids of the accessible account balances
	  */
	def creatorIds(implicit connection: Connection) = pullColumn(model.creatorIdColumn).flatMap { v => v.int }
	
	/**
	  * creation times of the accessible account balances
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	/**
	  * deprecation times of the accessible account balances
	  */
	def deprecationTimes(implicit connection: Connection) = 
		pullColumn(model.deprecatedAfterColumn).flatMap { v => v.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AccountBalanceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AccountBalanceFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyAccountBalancesAccess = 
		new ManyAccountBalancesAccess.ManyAccountBalancesSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the account ids of the targeted account balances
	  * @param newAccountId A new account id to assign
	  * @return Whether any account balance was affected
	  */
	def accountIds_=(newAccountId: Int)(implicit connection: Connection) = 
		putColumn(model.accountIdColumn, newAccountId)
	
	/**
	  * Updates the balances of the targeted account balances
	  * @param newBalance A new balance to assign
	  * @return Whether any account balance was affected
	  */
	def balances_=(newBalance: Double)(implicit connection: Connection) = 
		putColumn(model.balanceColumn, newBalance)
	
	/**
	  * Updates the creation times of the targeted account balances
	  * @param newCreated A new created to assign
	  * @return Whether any account balance was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted account balances
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any account balance was affected
	  */
	def creatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the deprecation times of the targeted account balances
	  * @param newDeprecatedAfter A new deprecated after to assign
	  * @return Whether any account balance was affected
	  */
	def deprecationTimes_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.deprecatedAfterColumn, newDeprecatedAfter)
}

