package vf.arbiter.core.model.partial.invoice

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible

/**
  * Represents different categories a unit can belong to. Units within a category can be compared.
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class UnitCategoryData() extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model.empty
}

