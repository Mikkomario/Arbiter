package vf.arbiter.command.model.stored.environment

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.command.database.access.single.environment.DbSingleDescriptionImport
import vf.arbiter.command.model.partial.environment.DescriptionImportData

/**
  * Represents a DescriptionImport that has already been stored in the database
  * @param id id of this DescriptionImport in the database
  * @param data Wrapped DescriptionImport data
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
case class DescriptionImport(id: Int, data: DescriptionImportData) 
	extends StoredModelConvertible[DescriptionImportData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this DescriptionImport in the database
	  */
	def access = DbSingleDescriptionImport(id)
}

