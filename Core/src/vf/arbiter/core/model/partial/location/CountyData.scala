package vf.arbiter.core.model.partial.location

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._

/**
  * Represents a county within a country
  * @param name County name, with that county's or country's primary language
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class CountyData(name: String) extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("name" -> name))
}

