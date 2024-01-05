package vf.arbiter.accounting.database.model.account

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import utopia.vault.nosql.storable.deprecation.DeprecatableAfter
import vf.arbiter.accounting.database.factory.account.AccountBalanceFactory
import vf.arbiter.accounting.model.partial.account.AccountBalanceData
import vf.arbiter.accounting.model.stored.account.AccountBalance

import java.time.Instant

/**
  * Used for constructing AccountBalanceModel instances and for inserting account balances to the database
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object AccountBalanceModel 
	extends DataInserter[AccountBalanceModel, AccountBalance, AccountBalanceData] 
		with DeprecatableAfter[AccountBalanceModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains account balance account id
	  */
	val accountIdAttName = "accountId"
	
	/**
	  * Name of the property that contains account balance balance
	  */
	val balanceAttName = "balance"
	
	/**
	  * Name of the property that contains account balance creator id
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains account balance created
	  */
	val createdAttName = "created"
	
	/**
	  * Name of the property that contains account balance deprecated after
	  */
	val deprecatedAfterAttName = "deprecatedAfter"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains account balance account id
	  */
	def accountIdColumn = table(accountIdAttName)
	
	/**
	  * Column that contains account balance balance
	  */
	def balanceColumn = table(balanceAttName)
	
	/**
	  * Column that contains account balance creator id
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains account balance created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains account balance deprecated after
	  */
	def deprecatedAfterColumn = table(deprecatedAfterAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = AccountBalanceFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: AccountBalanceData) = 
		apply(None, Some(data.accountId), Some(data.balance), data.creatorId, Some(data.created), 
			data.deprecatedAfter)
	
	override protected def complete(id: Value, data: AccountBalanceData) = AccountBalance(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param accountId Id of the described bank account
	  * @return A model containing only the specified account id
	  */
	def withAccountId(accountId: Int) = apply(accountId = Some(accountId))
	
	/**
	  * @param balance The amount of € on this account in this instance
	  * @return A model containing only the specified balance
	  */
	def withBalance(balance: Double) = apply(balance = Some(balance))
	
	/**
	  * 
		@param created Time when this value was specified. Also represents the time when this value was accurate.
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who provided this information. None if not known or if not applicable.
	  * @return A model containing only the specified creator id
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param deprecatedAfter Time when this statement was cancelled. None while valid.
	  * @return A model containing only the specified deprecated after
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = Some(deprecatedAfter))
	
	/**
	  * @param id A account balance id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
}

/**
  * Used for interacting with AccountBalances in the database
  * @param id account balance database id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class AccountBalanceModel(id: Option[Int] = None, accountId: Option[Int] = None, 
	balance: Option[Double] = None, creatorId: Option[Int] = None, created: Option[Instant] = None, 
	deprecatedAfter: Option[Instant] = None) 
	extends StorableWithFactory[AccountBalance]
{
	// IMPLEMENTED	--------------------
	
	override def factory = AccountBalanceModel.factory
	
	override def valueProperties = {
		import AccountBalanceModel._
		Vector("id" -> id, accountIdAttName -> accountId, balanceAttName -> balance, 
			creatorIdAttName -> creatorId, createdAttName -> created, 
			deprecatedAfterAttName -> deprecatedAfter)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param accountId Id of the described bank account
	  * @return A new copy of this model with the specified account id
	  */
	def withAccountId(accountId: Int) = copy(accountId = Some(accountId))
	
	/**
	  * @param balance The amount of € on this account in this instance
	  * @return A new copy of this model with the specified balance
	  */
	def withBalance(balance: Double) = copy(balance = Some(balance))
	
	/**
	  * 
		@param created Time when this value was specified. Also represents the time when this value was accurate.
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who provided this information. None if not known or if not applicable.
	  * @return A new copy of this model with the specified creator id
	  */
	def withCreatorId(creatorId: Int) = copy(creatorId = Some(creatorId))
	
	/**
	  * @param deprecatedAfter Time when this statement was cancelled. None while valid.
	  * @return A new copy of this model with the specified deprecated after
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
}

