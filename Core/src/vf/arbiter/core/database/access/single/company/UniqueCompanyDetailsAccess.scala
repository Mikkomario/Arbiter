package vf.arbiter.core.database.access.single.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.company.CompanyDetailsFactory
import vf.arbiter.core.database.model.company.CompanyDetailsModel
import vf.arbiter.core.model.stored.company.CompanyDetails

/**
  * A common trait for access points that return individual and distinct CompanyDetails.
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait UniqueCompanyDetailsAccess 
	extends SingleRowModelAccess[CompanyDetails] 
		with DistinctModelAccess[CompanyDetails, Option[CompanyDetails], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the company which this describes. None if no instance (or value) was found.
	  */
	def companyId(implicit connection: Connection) = pullColumn(model.companyIdColumn).int
	
	/**
	  * Name of this company. None if no instance (or value) was found.
	  */
	def name(implicit connection: Connection) = pullColumn(model.nameColumn).string
	
	/**
	  * Street address of this company's headquarters or operation. None if no instance (or value) was found.
	  */
	def addressId(implicit connection: Connection) = pullColumn(model.addressIdColumn).int
	
	/**
	  * Tax-related identifier code for this company. None if no instance (or value) was found.
	  */
	def taxCode(implicit connection: Connection) = pullColumn(model.taxCodeColumn).string
	
	/**
	  * Id of the user who wrote this description. None if no instance (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this CompanyDetails was first created. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Time when this CompanyDetails became deprecated. None while this CompanyDetails is still valid.. None if no instance (or value) was found.
	  */
	def deprecatedAfter(implicit connection: Connection) = pullColumn(model.deprecatedAfterColumn).instant
	
	/**
	  * Whether this information is by the company which is being described, 
		having a more authority. None if no instance (or value) was found.
	  */
	def isOfficial(implicit connection: Connection) = pullColumn(model.isOfficialColumn).boolean
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyDetailsModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyDetailsFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the addressId of the targeted CompanyDetails instance(s)
	  * @param newAddressId A new addressId to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def addressId_=(newAddressId: Int)(implicit connection: Connection) = 
		putColumn(model.addressIdColumn, newAddressId)
	
	/**
	  * Updates the companyId of the targeted CompanyDetails instance(s)
	  * @param newCompanyId A new companyId to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def companyId_=(newCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.companyIdColumn, newCompanyId)
	
	/**
	  * Updates the created of the targeted CompanyDetails instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted CompanyDetails instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the deprecatedAfter of the targeted CompanyDetails instance(s)
	  * @param newDeprecatedAfter A new deprecatedAfter to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def deprecatedAfter_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.deprecatedAfterColumn, newDeprecatedAfter)
	
	/**
	  * Updates the isOfficial of the targeted CompanyDetails instance(s)
	  * @param newIsOfficial A new isOfficial to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def isOfficial_=(newIsOfficial: Boolean)(implicit connection: Connection) = 
		putColumn(model.isOfficialColumn, newIsOfficial)
	
	/**
	  * Updates the name of the targeted CompanyDetails instance(s)
	  * @param newName A new name to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def name_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
	
	/**
	  * Updates the taxCode of the targeted CompanyDetails instance(s)
	  * @param newTaxCode A new taxCode to assign
	  * @return Whether any CompanyDetails instance was affected
	  */
	def taxCode_=(newTaxCode: String)(implicit connection: Connection) = 
		putColumn(model.taxCodeColumn, newTaxCode)
}

