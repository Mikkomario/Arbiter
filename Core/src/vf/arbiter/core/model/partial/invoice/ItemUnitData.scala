package vf.arbiter.core.model.partial.invoice

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._

/**
  * Represents a unit in which items can be counted
  * @param categoryId Id of the category this unit belongs to
  * @param multiplier A multiplier that, when applied to this unit, makes it comparable 
	with the other units in the same category
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class ItemUnitData(categoryId: Int, multiplier: Double = 1.0) extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("category_id" -> categoryId, "multiplier" -> multiplier))
}

