package vf.arbiter.core.database.model.company

import java.time.Instant
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.company.OrganizationCompanyFactory
import vf.arbiter.core.model.partial.company.OrganizationCompanyData
import vf.arbiter.core.model.stored.company.OrganizationCompany

/**
  * Used for constructing OrganizationCompanyModel instances and for inserting OrganizationCompanys to the database
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object OrganizationCompanyModel 
	extends DataInserter[OrganizationCompanyModel, OrganizationCompany, OrganizationCompanyData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains OrganizationCompany organizationId
	  */
	val organizationIdAttName = "organizationId"
	
	/**
	  * Name of the property that contains OrganizationCompany companyId
	  */
	val companyIdAttName = "companyId"
	
	/**
	  * Name of the property that contains OrganizationCompany creatorId
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains OrganizationCompany created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains OrganizationCompany organizationId
	  */
	def organizationIdColumn = table(organizationIdAttName)
	
	/**
	  * Column that contains OrganizationCompany companyId
	  */
	def companyIdColumn = table(companyIdAttName)
	
	/**
	  * Column that contains OrganizationCompany creatorId
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains OrganizationCompany created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = OrganizationCompanyFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: OrganizationCompanyData) = 
		apply(None, Some(data.organizationId), Some(data.companyId), data.creatorId, Some(data.created))
	
	override def complete(id: Value, data: OrganizationCompanyData) = OrganizationCompany(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param companyId Id of the owned company
	  * @return A model containing only the specified companyId
	  */
	def withCompanyId(companyId: Int) = apply(companyId = Some(companyId))
	
	/**
	  * @param created Time when this OrganizationCompany was first created
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who created this link
	  * @return A model containing only the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param id A OrganizationCompany id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param organizationId Id of the owner organization
	  * @return A model containing only the specified organizationId
	  */
	def withOrganizationId(organizationId: Int) = apply(organizationId = Some(organizationId))
}

/**
  * Used for interacting with OrganizationCompanies in the database
  * @param id OrganizationCompany database id
  * @param organizationId Id of the owner organization
  * @param companyId Id of the owned company
  * @param creatorId Id of the user who created this link
  * @param created Time when this OrganizationCompany was first created
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class OrganizationCompanyModel(id: Option[Int] = None, organizationId: Option[Int] = None, 
	companyId: Option[Int] = None, creatorId: Option[Int] = None, created: Option[Instant] = None) 
	extends StorableWithFactory[OrganizationCompany]
{
	// IMPLEMENTED	--------------------
	
	override def factory = OrganizationCompanyModel.factory
	
	override def valueProperties = 
	{
		import OrganizationCompanyModel._
		Vector("id" -> id, organizationIdAttName -> organizationId, companyIdAttName -> companyId, 
			creatorIdAttName -> creatorId, createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
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
	  * @param organizationId A new organizationId
	  * @return A new copy of this model with the specified organizationId
	  */
	def withOrganizationId(organizationId: Int) = copy(organizationId = Some(organizationId))
}

