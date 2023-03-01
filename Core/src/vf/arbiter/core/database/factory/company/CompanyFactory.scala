package vf.arbiter.core.database.factory.company

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.company.CompanyData
import vf.arbiter.core.model.stored.company.Company

/**
  * Used for reading Company data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object CompanyFactory extends FromValidatedRowModelFactory[Company] with FromRowFactoryWithTimestamps[Company]
{
	// IMPLEMENTED	--------------------
	
	override def creationTimePropertyName = "created"
	
	override def table = CoreTables.company
	
	override def fromValidatedModel(valid: Model) =
		Company(valid("id").getInt, CompanyData(valid("yCode").getString, valid("creatorId").int, 
			valid("created").getInstant))
}

