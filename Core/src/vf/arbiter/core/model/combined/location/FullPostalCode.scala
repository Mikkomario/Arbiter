package vf.arbiter.core.model.combined.location

import utopia.flow.util.Extender
import vf.arbiter.core.model.partial.location.PostalCodeData
import vf.arbiter.core.model.stored.location.{County, PostalCode}

/**
  * Combines code with County data
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class FullPostalCode(code: PostalCode, county: County) extends Extender[PostalCodeData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this code in the database
	  */
	def id = code.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = code.data
	
	override def toString = s"${code.number} ${county.name}"
}

