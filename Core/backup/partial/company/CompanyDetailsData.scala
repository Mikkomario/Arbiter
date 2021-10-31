package vf.arbiter.core.model.partial.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now
import vf.arbiter.core.database.access.single.location.DbSingleStreetAddress
import vf.arbiter.core.model.template.Exportable

/**
  * Contains company information which may change and on which there may be varying views
  * @param companyId Id of the company which this describes
  * @param name Name of this company
  * @param addressId Street address of this company's headquarters or operation
  * @param taxCode Tax-related identifier code for this company
  * @param creatorId Id of the user who wrote this description
  * @param created Time when this CompanyDetails was first created
  * @param deprecatedAfter Time when this CompanyDetails became deprecated. None while this CompanyDetails is still valid.
  * @param isOfficial Whether this information is by the company which is being described, 
	having a more authority
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class CompanyDetailsData(companyId: Int, name: String, addressId: Int, taxCode: Option[String] = None, 
	creatorId: Option[Int] = None, created: Instant = Now, deprecatedAfter: Option[Instant] = None, 
	isOfficial: Boolean = false) 
	extends ModelConvertible with Exportable
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this CompanyDetails has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	/**
	  * Whether this CompanyDetails is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	/**
	 * @return An access point to this company's address
	 */
	def addressAccess = DbSingleStreetAddress(addressId)
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("company_id" -> companyId, "name" -> name, "address_id" -> addressId, 
			"tax_code" -> taxCode, "creator_id" -> creatorId, "created" -> created, 
			"deprecated_after" -> deprecatedAfter, "is_official" -> isOfficial))
	
	override def toExportModel =
		Model(Vector("name" -> name, "tax_code" -> taxCode, "is_official" -> isOfficial, "created" -> created,
			"deprecated_after" -> deprecatedAfter))
}

