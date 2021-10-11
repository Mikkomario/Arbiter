package vf.arbiter.core.model.partial.company

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._

/**
  * Represents a registered company (or an individual person)
  * @param yCode Official registration code of this company (id in the country system)
  * @param name Name of this company
  * @param addressId Street address of this company's headquarters or operation
  * @param taxCode Tax-related identifier code for this company
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class CompanyData(yCode: String, name: String, addressId: Int, taxCode: Option[String] = None) 
	extends ModelConvertible
{
	// COMPUTED ------------------------
	
	/**
	 * @return This company's name and y-code back to back
	 */
	def nameAndYCode = s"$name $yCode"
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel =
		Model(Vector("y_code" -> yCode, "name" -> name, "address_id" -> addressId, "tax_code" -> taxCode))
}

