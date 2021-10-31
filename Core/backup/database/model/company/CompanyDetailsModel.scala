package vf.arbiter.core.database.model.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import utopia.vault.nosql.storable.deprecation.DeprecatableAfter
import vf.arbiter.core.database.factory.company.CompanyDetailsFactory
import vf.arbiter.core.model.partial.company.CompanyDetailsData
import vf.arbiter.core.model.stored.company.CompanyDetails

/**
  * Used for constructing CompanyDetailsModel instances and for inserting CompanyDetailss to the database
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object CompanyDetailsModel 
	extends DataInserter[CompanyDetailsModel, CompanyDetails, CompanyDetailsData] 
		with DeprecatableAfter[CompanyDetailsModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains CompanyDetails companyId
	  */
	val companyIdAttName = "companyId"
	/**
	  * Name of the property that contains CompanyDetails name
	  */
	val nameAttName = "name"
	/**
	  * Name of the property that contains CompanyDetails addressId
	  */
	val addressIdAttName = "addressId"
	/**
	  * Name of the property that contains CompanyDetails taxCode
	  */
	val taxCodeAttName = "taxCode"
	/**
	  * Name of the property that contains CompanyDetails creatorId
	  */
	val creatorIdAttName = "creatorId"
	/**
	  * Name of the property that contains CompanyDetails created
	  */
	val createdAttName = "created"
	/**
	  * Name of the property that contains CompanyDetails deprecatedAfter
	  */
	val deprecatedAfterAttName = "deprecatedAfter"
	/**
	  * Name of the property that contains CompanyDetails isOfficial
	  */
	val isOfficialAttName = "isOfficial"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains CompanyDetails companyId
	  */
	def companyIdColumn = table(companyIdAttName)
	/**
	  * Column that contains CompanyDetails name
	  */
	def nameColumn = table(nameAttName)
	/**
	  * Column that contains CompanyDetails addressId
	  */
	def addressIdColumn = table(addressIdAttName)
	/**
	  * Column that contains CompanyDetails taxCode
	  */
	def taxCodeColumn = table(taxCodeAttName)
	/**
	  * Column that contains CompanyDetails creatorId
	  */
	def creatorIdColumn = table(creatorIdAttName)
	/**
	  * Column that contains CompanyDetails created
	  */
	def createdColumn = table(createdAttName)
	/**
	  * Column that contains CompanyDetails deprecatedAfter
	  */
	def deprecatedAfterColumn = table(deprecatedAfterAttName)
	/**
	  * Column that contains CompanyDetails isOfficial
	  */
	def isOfficialColumn = table(isOfficialAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = CompanyDetailsFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: CompanyDetailsData) = 
		apply(None, Some(data.companyId), Some(data.name), Some(data.addressId), data.taxCode, 
			data.creatorId, Some(data.created), data.deprecatedAfter, Some(data.isOfficial))
	
	override def complete(id: Value, data: CompanyDetailsData) = CompanyDetails(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	 * @param namePart Part of a company's name
	 * @return A condition that returns company details where their name contains that string
	 */
	def nameMatchCondition(namePart: String) = nameColumn.contains(namePart)
	
	/**
	  * @param addressId Street address of this company's headquarters or operation
	  * @return A model containing only the specified addressId
	  */
	def withAddressId(addressId: Int) = apply(addressId = Some(addressId))
	/**
	  * @param companyId Id of the company which this describes
	  * @return A model containing only the specified companyId
	  */
	def withCompanyId(companyId: Int) = apply(companyId = Some(companyId))
	/**
	  * @param created Time when this CompanyDetails was first created
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	/**
	  * @param creatorId Id of the user who wrote this description
	  * @return A model containing only the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	/**
	  * @param deprecatedAfter Time when this CompanyDetails became deprecated. None while this CompanyDetails is still valid.
	  * @return A model containing only the specified deprecatedAfter
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = Some(deprecatedAfter))
	/**
	  * @param id A CompanyDetails id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	/**
	  * @param isOfficial Whether this information is by the company which is being described, 
		having a more authority
	  * @return A model containing only the specified isOfficial
	  */
	def withIsOfficial(isOfficial: Boolean) = apply(isOfficial = Some(isOfficial))
	/**
	  * @param name Name of this company
	  * @return A model containing only the specified name
	  */
	def withName(name: String) = apply(name = Some(name))
	/**
	  * @param taxCode Tax-related identifier code for this company
	  * @return A model containing only the specified taxCode
	  */
	def withTaxCode(taxCode: String) = apply(taxCode = Some(taxCode))
}

/**
  * Used for interacting with CompanyDetails in the database
  * @param id CompanyDetails database id
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
case class CompanyDetailsModel(id: Option[Int] = None, companyId: Option[Int] = None, 
	name: Option[String] = None, addressId: Option[Int] = None, taxCode: Option[String] = None, 
	creatorId: Option[Int] = None, created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None, 
	isOfficial: Option[Boolean] = None) 
	extends StorableWithFactory[CompanyDetails]
{
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyDetailsModel.factory
	
	override def valueProperties = 
	{
		import CompanyDetailsModel._
		Vector("id" -> id, companyIdAttName -> companyId, nameAttName -> name, addressIdAttName -> addressId, 
			taxCodeAttName -> taxCode, creatorIdAttName -> creatorId, createdAttName -> created, 
			deprecatedAfterAttName -> deprecatedAfter, isOfficialAttName -> isOfficial)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param addressId A new addressId
	  * @return A new copy of this model with the specified addressId
	  */
	def withAddressId(addressId: Int) = copy(addressId = Some(addressId))
	
	/**
	  * @param companyId A new companyId
	  * @return A new copy of this model with the specified companyId
	  */
	def withCompanyId(companyId: Int) = copy(companyId = Some(companyId))
	
	/**
	  * @param created A new created
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param creatorId A new creatorId
	  * @return A new copy of this model with the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = copy(creatorId = Some(creatorId))
	
	/**
	  * @param deprecatedAfter A new deprecatedAfter
	  * @return A new copy of this model with the specified deprecatedAfter
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	/**
	  * @param isOfficial A new isOfficial
	  * @return A new copy of this model with the specified isOfficial
	  */
	def withIsOfficial(isOfficial: Boolean) = copy(isOfficial = Some(isOfficial))
	
	/**
	  * @param name A new name
	  * @return A new copy of this model with the specified name
	  */
	def withName(name: String) = copy(name = Some(name))
	
	/**
	  * @param taxCode A new taxCode
	  * @return A new copy of this model with the specified taxCode
	  */
	def withTaxCode(taxCode: String) = copy(taxCode = Some(taxCode))
}

