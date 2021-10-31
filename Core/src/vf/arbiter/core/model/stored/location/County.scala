package vf.arbiter.core.model.stored.location

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.location.DbSingleCounty
import vf.arbiter.core.model.partial.location.CountyData

/**
  * Represents a County that has already been stored in the database
  * @param id id of this County in the database
  * @param data Wrapped County data
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class County(id: Int, data: CountyData) extends StoredModelConvertible[CountyData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this County in the database
	  */
	def access = DbSingleCounty(id)
}

