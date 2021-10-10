package vf.arbiter.core.model.partial.company

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._

/**
  * Represents a bank (used with bank addresses)
  * @param name (Short) name of this bank
  * @param bic BIC-code of this bank
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class BankData(name: String, bic: String) extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("name" -> name, "bic" -> bic))
}

