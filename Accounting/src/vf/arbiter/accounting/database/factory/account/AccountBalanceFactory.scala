package vf.arbiter.accounting.database.factory.account

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.accounting.database.ArbiterAccountingTables
import vf.arbiter.accounting.database.model.account.AccountBalanceModel
import vf.arbiter.accounting.model.partial.account.AccountBalanceData
import vf.arbiter.accounting.model.stored.account.AccountBalance

/**
  * Used for reading account balance data from the DB
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object AccountBalanceFactory 
	extends FromValidatedRowModelFactory[AccountBalance] with FromRowFactoryWithTimestamps[AccountBalance] 
		with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def creationTimePropertyName = "created"
	
	override def nonDeprecatedCondition = AccountBalanceModel.nonDeprecatedCondition
	
	override def table = ArbiterAccountingTables.accountBalance
	
	override protected def fromValidatedModel(valid: Model) = 
		AccountBalance(valid("id").getInt, AccountBalanceData(valid("accountId").getInt, 
			valid("balance").getDouble, valid("creatorId").int, valid("created").getInstant, 
			valid("deprecatedAfter").instant))
}

