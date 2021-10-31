package vf.arbiter.core.database.access.many.company

import java.time.Instant
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.company.CompanyDetailsFactory
import vf.arbiter.core.database.model.company.CompanyDetailsModel
import vf.arbiter.core.model.stored.company.CompanyDetails

object ManyCompanyDetailsAccess
{
	// NESTED	--------------------
	
	private class ManyCompanyDetailsSubView(override val parent: ManyRowModelAccess[CompanyDetails], 
		override val filterCondition: Condition) 
		extends ManyCompanyDetailsAccess with SubView
}

/**
  * A common trait for access points which target multiple CompanyDetails at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait ManyCompanyDetailsAccess extends ManyRowModelAccess[CompanyDetails] with Indexed
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
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyDetailsFactory
	
	override protected def defaultOrdering = None
	
	override def filter(additionalCondition: Condition): ManyCompanyDetailsAccess = 
		new ManyCompanyDetailsAccess.ManyCompanyDetailsSubView(this, additionalCondition)
	
	
	// OTHER	--------------------
	
	/** Finds companies within this group that contain the specified string in their name
	 * @param companyNamePart String that must be contained within a company name
	* @param connection Implicit DB Connection
	* @return Companies that have the specified string in their name
	*/
	def matchingName(companyNamePart: String)(implicit connection: Connection) =
		find(model.nameMatchCondition(companyNamePart))
	
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

