package vf.arbiter.accounting.model.stored.account

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.accounting.database.access.single.account.balance.DbSingleAccountBalance
import vf.arbiter.accounting.model.partial.account.AccountBalanceData

/**
  * Represents a account balance that has already been stored in the database
  * @param id id of this account balance in the database
  * @param data Wrapped account balance data
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class AccountBalance(id: Int, data: AccountBalanceData) 
	extends StoredModelConvertible[AccountBalanceData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this account balance in the database
	  */
	def access = DbSingleAccountBalance(id)
}

