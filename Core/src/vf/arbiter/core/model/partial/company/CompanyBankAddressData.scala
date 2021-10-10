package vf.arbiter.core.model.partial.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now

/**
  * Used for listing which bank addresses belong to which company
  * @param companyId Id of the company which owns this bank account address
  * @param bankId Id of the bank where the company owns an account
  * @param address The linked bank account address
  * @param created Time when this information was registered
  * @param isDefault Whether this is the preferred / primary bank address used by this company
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class CompanyBankAddressData(companyId: Int, bankId: Int, address: String, created: Instant = Now, 
	isDefault: Boolean = false) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("company_id" -> companyId, "bank_id" -> bankId, "address" -> address, 
			"created" -> created, "is_default" -> isDefault))
}

