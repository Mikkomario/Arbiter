package vf.arbiter.core.database.factory.location

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.location.PostalCodeData
import vf.arbiter.core.model.stored.location.PostalCode

/**
  * Used for reading PostalCode data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object PostalCodeFactory extends FromValidatedRowModelFactory[PostalCode]
{
	// IMPLEMENTED	--------------------
	
	override def table = CoreTables.postalCode
	
	override def defaultOrdering = None
	
	override def fromValidatedModel(valid: Model) =
		PostalCode(valid("id").getInt, PostalCodeData(valid("number").getString, valid("countyId").getInt, 
			valid("creatorId").int, valid("created").getInstant))
}

