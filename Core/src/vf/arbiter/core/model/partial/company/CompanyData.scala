package vf.arbiter.core.model.partial.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now

/**
  * Represents a registered company (or an individual person)
  * @param yCode Official registration code of this company (id in the country system)
  * @param creatorId Id of the user who registered this company to this system
  * @param created Time when this company was registered
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class CompanyData(yCode: String, creatorId: Option[Int] = None, created: Instant = Now) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("y_code" -> yCode, "creator_id" -> creatorId, "created" -> created))
}

