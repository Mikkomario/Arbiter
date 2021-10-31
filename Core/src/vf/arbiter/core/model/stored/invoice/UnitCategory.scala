package vf.arbiter.core.model.stored.invoice

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.invoice.DbSingleUnitCategory
import vf.arbiter.core.model.partial.invoice.UnitCategoryData

/**
  * Represents a UnitCategory that has already been stored in the database
  * @param id id of this UnitCategory in the database
  * @param data Wrapped UnitCategory data
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class UnitCategory(id: Int, data: UnitCategoryData) extends StoredModelConvertible[UnitCategoryData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this UnitCategory in the database
	  */
	def access = DbSingleUnitCategory(id)
}

