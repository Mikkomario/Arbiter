package vf.arbiter.core.model.combined.location

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.view.template.Extender
import vf.arbiter.core.model.partial.location.PostalCodeData
import vf.arbiter.core.model.stored.location.{County, PostalCode}
import vf.arbiter.core.model.template.Exportable

/**
  * Combines code with County data
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class FullPostalCode(code: PostalCode, county: County) extends Extender[PostalCodeData] with Exportable
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this code in the database
	  */
	def id = code.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = code.data
	
	override def toString = s"${code.number} ${county.name}"
	
	override def toExportModel = Model(Vector("county" -> county.name, "code" -> code.number))
}

