package vf.arbiter.core.database.access.many.company

import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.arbiter.core.database.model.company.CompanyDetailsModel

import java.time.Instant

/**
  * A common trait for access points which target multiple CompanyDetails or similar instances at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait ManyCompanyDetailsAccessLike[+A, +Repr <: ManyModelAccess[A]]
	extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * companyIds of the accessible CompanyDetails
	  */
	def companyIds(implicit connection: Connection) = 
		pullColumn(model.companyIdColumn).flatMap { value => value.int }
	
	/**
	  * names of the accessible CompanyDetails
	  */
	def names(implicit connection: Connection) = pullColumn(model.nameColumn)
		.flatMap { value => value.string }
	
	/**
	  * addressIds of the accessible CompanyDetails
	  */
	def addressIds(implicit connection: Connection) = 
		pullColumn(model.addressIdColumn).flatMap { value => value.int }
	
	/**
	  * taxCodes of the accessible CompanyDetails
	  */
	def taxCodes(implicit connection: Connection) = 
		pullColumn(model.taxCodeColumn).flatMap { value => value.string }
	
	/**
	  * creatorIds of the accessible CompanyDetails
	  */
	def creatorIds(implicit connection: Connection) = 
		pullColumn(model.creatorIdColumn).flatMap { value => value.int }
	
	/**
	  * creationTimes of the accessible CompanyDetails
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	
	/**
	  * deprecationTimes of the accessible CompanyDetails
	  */
	def deprecationTimes(implicit connection: Connection) = 
		pullColumn(model.deprecatedAfterColumn).flatMap { value => value.instant }
	
	/**
	  * areOfficial of the accessible CompanyDetails
	  */
	def areOfficial(implicit connection: Connection) = 
		pullColumn(model.isOfficialColumn).flatMap { value => value.boolean }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyDetailsModel
	
	
	// OTHER	--------------------
	
	/** Finds companies within this group that contain the specified string in their name
	 * @param companyNamePart String that must be contained within a company name
	* @param connection Implicit DB Connection
	* @return Companies that have the specified string in their name
	*/
	def matchingName(companyNamePart: String)(implicit connection: Connection) =
		find(model.nameMatchCondition(companyNamePart))
	
	/**
	 * Deprecates all accessible company details
	 * @param connection Implicit Db Connection
	 * @return Whether any row was targeted
	 */
	def deprecate()(implicit connection: Connection) = deprecationTimes = Now
	
	/**
	  * Updates the addressId of the targeted CompanyDetails instance(s)
	  * @param newAddressId A new addressId to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def addressIds_=(newAddressId: Int)(implicit connection: Connection) = 
		putColumn(model.addressIdColumn, newAddressId)
	
	/**
	  * Updates the isOfficial of the targeted CompanyDetails instance(s)
	  * @param newIsOfficial A new isOfficial to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def areOfficial_=(newIsOfficial: Boolean)(implicit connection: Connection) = 
		putColumn(model.isOfficialColumn, newIsOfficial)
	
	/**
	  * Updates the companyId of the targeted CompanyDetails instance(s)
	  * @param newCompanyId A new companyId to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def companyIds_=(newCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.companyIdColumn, newCompanyId)
	
	/**
	  * Updates the created of the targeted CompanyDetails instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted CompanyDetails instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def creatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the deprecatedAfter of the targeted CompanyDetails instance(s)
	  * @param newDeprecatedAfter A new deprecatedAfter to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def deprecationTimes_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.deprecatedAfterColumn, newDeprecatedAfter)
	
	/**
	  * Updates the name of the targeted CompanyDetails instance(s)
	  * @param newName A new name to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def names_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
	
	/**
	  * Updates the taxCode of the targeted CompanyDetails instance(s)
	  * @param newTaxCode A new taxCode to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def taxCodes_=(newTaxCode: String)(implicit connection: Connection) = 
		putColumn(model.taxCodeColumn, newTaxCode)
}

