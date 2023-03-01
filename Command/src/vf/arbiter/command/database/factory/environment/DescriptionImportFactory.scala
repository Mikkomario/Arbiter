package vf.arbiter.command.database.factory.environment

import utopia.flow.generic.model.immutable.Model
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.command.database.CommandTables
import vf.arbiter.command.model.partial.environment.DescriptionImportData
import vf.arbiter.command.model.stored.environment.DescriptionImport

/**
  * Used for reading DescriptionImport data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
object DescriptionImportFactory extends FromValidatedRowModelFactory[DescriptionImport]
{
	// IMPLEMENTED	--------------------
	
	override def table = CommandTables.descriptionImport
	
	override def defaultOrdering = None
	
	override def fromValidatedModel(valid: Model) =
		DescriptionImport(valid("id").getInt, DescriptionImportData(valid("Path").getString, 
			valid("Created").getInstant))
}

