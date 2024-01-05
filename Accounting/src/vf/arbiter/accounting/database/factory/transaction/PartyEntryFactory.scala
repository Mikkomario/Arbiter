package vf.arbiter.accounting.database.factory.transaction

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.accounting.database.ArbiterAccountingTables
import vf.arbiter.accounting.model.partial.transaction.PartyEntryData
import vf.arbiter.accounting.model.stored.transaction.PartyEntry

/**
  * Used for reading party entry data from the DB
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object PartyEntryFactory extends FromValidatedRowModelFactory[PartyEntry]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = ArbiterAccountingTables.partyEntry
	
	override protected def fromValidatedModel(valid: Model) = 
		PartyEntry(valid("id").getInt, PartyEntryData(valid("name").getString, valid("created").getInstant))
}

