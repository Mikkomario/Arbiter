package vf.arbiter.core.model.partial.location

import java.time.Instant
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.time.Now

/**
  * Represents a postal code, which represents an area within a county
  * @param number The number portion of this postal code
  * @param countyId Id of the county where this postal code is resides
  * @param creatorId Id of the user linked with this PostalCode
  * @param created Time when this PostalCode was first created
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class PostalCodeData(number: String, countyId: Int, creatorId: Option[Int] = None, 
	created: Instant = Now) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("number" -> number, "county_id" -> countyId, "creator_id" -> creatorId, 
			"created" -> created))
}

