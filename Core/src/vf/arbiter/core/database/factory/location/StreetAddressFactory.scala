package vf.arbiter.core.database.factory.location

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.location.StreetAddressData
import vf.arbiter.core.model.stored.location.StreetAddress

/**
  * Used for reading StreetAddress data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object StreetAddressFactory extends FromValidatedRowModelFactory[StreetAddress]
{
	// IMPLEMENTED	--------------------
	
	override def table = CoreTables.streetAddress
	
	override def fromValidatedModel(valid: Model) =
		StreetAddress(valid("id").getInt, StreetAddressData(valid("postalCodeId").getInt, 
			valid("streetName").getString, valid("buildingNumber").getString, valid("stair").string, 
			valid("roomNumber").string, valid("creatorId").int, valid("created").getInstant))
}

