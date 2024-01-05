package vf.arbiter.accounting.database.access.single.account.balance

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.accounting.model.stored.account.AccountBalance

/**
  * An access point to individual account balances, based on their id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class DbSingleAccountBalance(id: Int) 
	extends UniqueAccountBalanceAccess with SingleIntIdModelAccess[AccountBalance]

