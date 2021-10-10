package vf.arbiter.core.model.partial.location

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._

/**
  * Represents a postal code, which represents an area within a county
  * @param number The number portion of this postal code
  * @param countyId Id of the county where this postal code is resides
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class PostalCodeData(number: String, countyId: Int) extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("number" -> number, "county_id" -> countyId))
}

