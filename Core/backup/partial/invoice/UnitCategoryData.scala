package vf.arbiter.core.model.partial.invoice

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible

/**
  * Represents different categories a unit can belong to. Units within a category can be compared.
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class UnitCategoryData() extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model.empty
}

