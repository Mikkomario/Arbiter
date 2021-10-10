package vf.arbiter.core.database.factory.location

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.location.PostalCodeData
import vf.arbiter.core.model.stored.location.PostalCode

/**
  * Used for reading PostalCode data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object PostalCodeFactory extends FromValidatedRowModelFactory[PostalCode]
{
	// IMPLEMENTED	--------------------
	
	override def table = CoreTables.postalCode
	
	override def fromValidatedModel(valid: Model[Constant]) = 
		PostalCode(valid("id").getInt, PostalCodeData(valid("number").getString, valid("countyId").getInt))
}

