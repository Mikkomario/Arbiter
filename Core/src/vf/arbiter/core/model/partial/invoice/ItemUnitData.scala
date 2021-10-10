package vf.arbiter.core.model.partial.invoice

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible

/**
  * Represents a unit in which items can be counted
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class ItemUnitData() extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model.empty
}

