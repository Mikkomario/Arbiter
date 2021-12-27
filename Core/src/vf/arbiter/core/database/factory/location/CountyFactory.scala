package vf.arbiter.core.database.factory.location

import utopia.flow.datastructure.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.location.CountyData
import vf.arbiter.core.model.stored.location.County

/**
  * Used for reading County data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object CountyFactory extends FromValidatedRowModelFactory[County]
{
	// IMPLEMENTED	--------------------
	
	override def table = CoreTables.county
	
	override def defaultOrdering = None
	
	override def fromValidatedModel(valid: Model) =
		County(valid("id").getInt, CountyData(valid("name").getString, valid("creatorId").int, 
			valid("created").getInstant))
}

