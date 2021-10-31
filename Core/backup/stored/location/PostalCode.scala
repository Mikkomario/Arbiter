package vf.arbiter.core.model.stored.location

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.location.DbSinglePostalCode
import vf.arbiter.core.model.partial.location.PostalCodeData

/**
  * Represents a PostalCode that has already been stored in the database
  * @param id id of this PostalCode in the database
  * @param data Wrapped PostalCode data
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class PostalCode(id: Int, data: PostalCodeData) extends StoredModelConvertible[PostalCodeData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this PostalCode in the database
	  */
	def access = DbSinglePostalCode(id)
}

