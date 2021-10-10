package vf.arbiter.core.database.factory.company

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.company.CompanyBankAddressData
import vf.arbiter.core.model.stored.company.CompanyBankAddress

/**
  * Used for reading CompanyBankAddress data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object CompanyBankAddressFactory 
	extends FromValidatedRowModelFactory[CompanyBankAddress] 
		with FromRowFactoryWithTimestamps[CompanyBankAddress]
{
	// IMPLEMENTED	--------------------
	
	override def creationTimePropertyName = "created"
	
	override def table = CoreTables.companyBankAddress
	
	override def fromValidatedModel(valid: Model[Constant]) = 
		CompanyBankAddress(valid("id").getInt, CompanyBankAddressData(valid("companyId").getInt, 
			valid("bankId").getInt, valid("address").getString, valid("created").getInstant, 
			valid("isDefault").getBoolean))
}

