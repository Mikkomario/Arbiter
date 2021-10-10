package vf.arbiter.core.model.stored.location

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.model.partial.location.StreetAddressData

/**
  * Represents a StreetAddress that has already been stored in the database
  * @param id id of this StreetAddress in the database
  * @param data Wrapped StreetAddress data
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class StreetAddress(id: Int, data: StreetAddressData) extends StoredModelConvertible[StreetAddressData]

