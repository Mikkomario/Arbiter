package vf.arbiter.core.database.factory.company

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.database.model.company.CompanyBankAccountModel
import vf.arbiter.core.model.partial.company.CompanyBankAccountData
import vf.arbiter.core.model.stored.company.CompanyBankAccount

/**
  * Used for reading CompanyBankAccount data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object CompanyBankAccountFactory extends FromValidatedRowModelFactory[CompanyBankAccount] with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def nonDeprecatedCondition = CompanyBankAccountModel.nonDeprecatedCondition
	
	override def defaultOrdering = None
	
	override def table = CoreTables.companyBankAccount
	
	override def fromValidatedModel(valid: Model) =
		CompanyBankAccount(valid("id").getInt, CompanyBankAccountData(valid("companyId").getInt, 
			valid("bankId").getInt, valid("address").getString, valid("creatorId").int, 
			valid("created").getInstant, valid("deprecatedAfter").instant, valid("isOfficial").getBoolean))
}

