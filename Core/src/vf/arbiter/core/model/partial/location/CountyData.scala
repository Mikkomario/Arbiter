package vf.arbiter.core.model.partial.location

import java.time.Instant
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now

/**
  * Represents a county within a country
  * @param name County name, with that county's or country's primary language
  * @param creatorId Id of the user who registered this county
  * @param created Time when this County was first created
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class CountyData(name: String, creatorId: Option[Int] = None, created: Instant = Now) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("name" -> name, "creator_id" -> creatorId, "created" -> created))
}

