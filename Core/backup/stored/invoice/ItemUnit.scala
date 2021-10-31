package vf.arbiter.core.model.stored.invoice

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.invoice.DbSingleItemUnit
import vf.arbiter.core.model.partial.invoice.ItemUnitData

/**
  * Represents a ItemUnit that has already been stored in the database
  * @param id id of this ItemUnit in the database
  * @param data Wrapped ItemUnit data
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class ItemUnit(id: Int, data: ItemUnitData) extends StoredModelConvertible[ItemUnitData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this ItemUnit in the database
	  */
	def access = DbSingleItemUnit(id)
}

