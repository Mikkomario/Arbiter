package vf.arbiter.core.model.partial.location

import java.time.Instant
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.time.Now
import vf.arbiter.core.model.template.Exportable

/**
  * Represents a county within a country
  * @param name County name, with that county's or country's primary language
  * @param creatorId Id of the user who registered this county
  * @param created Time when this County was first created
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class CountyData(name: String, creatorId: Option[Int] = None, created: Instant = Now) 
	extends ModelConvertible with Exportable
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("name" -> name, "creator_id" -> creatorId, "created" -> created))
	
	override def toExportModel = Model(Vector("name" -> name))
}

