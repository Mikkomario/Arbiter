package vf.arbiter.core.model.partial.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now

/**
  * Used for listing which bank accounts belong to which company
  * @param companyId Id of the company which owns this bank account
  * @param bankId Id of the bank where the company owns an account
  * @param address The linked bank account address
  * @param creatorId Id of the user linked with this CompanyBankAccount
  * @param created Time when this information was registered
  * @param deprecatedAfter Time when this CompanyBankAccount became deprecated. None while this CompanyBankAccount is still valid.
  * @param isOfficial Whether this bank account information was written by the company authorities
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class CompanyBankAccountData(companyId: Int, bankId: Int, address: String, 
	creatorId: Option[Int] = None, created: Instant = Now, deprecatedAfter: Option[Instant] = None, 
	isOfficial: Boolean = false) 
	extends ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this CompanyBankAccount has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	
	/**
	  * Whether this CompanyBankAccount is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("company_id" -> companyId, "bank_id" -> bankId, "address" -> address, 
			"creator_id" -> creatorId, "created" -> created, "deprecated_after" -> deprecatedAfter, 
			"is_official" -> isOfficial))
}

