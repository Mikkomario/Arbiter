package vf.arbiter.core.database.factory.company

import utopia.flow.datastructure.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.company.BankData
import vf.arbiter.core.model.stored.company.Bank

/**
  * Used for reading Bank data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object BankFactory extends FromValidatedRowModelFactory[Bank]
{
	// IMPLEMENTED	--------------------
	
	override def table = CoreTables.bank
	
	override def defaultOrdering = None
	
	override def fromValidatedModel(valid: Model) =
		Bank(valid("id").getInt, BankData(valid("name").getString, valid("bic").getString, 
			valid("creatorId").int, valid("created").getInstant))
}

