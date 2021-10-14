package vf.arbiter.core.model.partial.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now

/**
  * Represents a bank (used with bank addresses)
  * @param name (Short) name of this bank
  * @param bic BIC-code of this bank
  * @param creatorId Id of the user who registered this address
  * @param created Time when this Bank was first created
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class BankData(name: String, bic: String, creatorId: Option[Int] = None, created: Instant = Now) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("name" -> name, "bic" -> bic, "creator_id" -> creatorId, "created" -> created))
}

