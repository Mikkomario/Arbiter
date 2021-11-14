package vf.arbiter.core.database.access.many.company

import utopia.citadel.database.CitadelTables
import utopia.citadel.database.factory.organization.MembershipFactory

import java.time.Instant
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.{Condition, Select, Where}
import utopia.vault.sql.SqlExtensions._
import vf.arbiter.core.database.factory.company.OrganizationCompanyFactory
import vf.arbiter.core.database.model.company.OrganizationCompanyModel
import vf.arbiter.core.model.stored.company.OrganizationCompany

object ManyOrganizationCompaniesAccess
{
	// NESTED	--------------------
	
	private class ManyOrganizationCompaniesSubView(override val parent: ManyRowModelAccess[OrganizationCompany], 
		override val filterCondition: Condition) 
		extends ManyOrganizationCompaniesAccess with SubView
}

/**
  * A common trait for access points which target multiple OrganizationCompanies at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait ManyOrganizationCompaniesAccess extends ManyRowModelAccess[OrganizationCompany] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	 * @param connection Implicit DB Connection
	 * @return Organization memberships associated with these links
	 */
	def memberships(implicit connection: Connection) =
	{
		val membershipTable = CitadelTables.membership
		// Joins to organization to membership
		// Only selects active memberships
		MembershipFactory(connection(Select(membershipTable join CitadelTables.organization join table, membershipTable) +
			Where(mergeCondition(MembershipFactory.nonDeprecatedCondition))))
	}
	
	/**
	  * organizationIds of the accessible OrganizationCompanies
	  */
	def organizationIds(implicit connection: Connection) = 
		pullColumn(model.organizationIdColumn).flatMap { value => value.int }
	
	/**
	  * companyIds of the accessible OrganizationCompanies
	  */
	def companyIds(implicit connection: Connection) = 
		pullColumn(model.companyIdColumn).flatMap { value => value.int }
	
	/**
	  * creatorIds of the accessible OrganizationCompanies
	  */
	def creatorIds(implicit connection: Connection) = 
		pullColumn(model.creatorIdColumn).flatMap { value => value.int }
	
	/**
	  * creationTimes of the accessible OrganizationCompanies
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = OrganizationCompanyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = OrganizationCompanyFactory
	
	override protected def defaultOrdering = None
	
	override def filter(additionalCondition: Condition): ManyOrganizationCompaniesAccess = 
		new ManyOrganizationCompaniesAccess.ManyOrganizationCompaniesSubView(this, additionalCondition)
	
	
	// OTHER	--------------------
	
	/**
	 * @param companyId A company id
	 * @return An access point to links between that company and organizations
	 */
	def linkedToCompanyWithId(companyId: Int) =
		filter(model.withCompanyId(companyId).toCondition)
	/**
	 * @param companyIds Ids of targeted companies
	 * @return An access point to organization-company links concerning any of those companies
	 */
	def linkedToAnyOfCompanies(companyIds: Iterable[Int]) =
		filter(model.companyIdColumn in companyIds)
	
	/**
	  * Updates the companyId of the targeted OrganizationCompany instance(s)
	  * @param newCompanyId A new companyId to assign
	  * @return Whether any OrganizationCompany instance was affected
	  */
	def companyIds_=(newCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.companyIdColumn, newCompanyId)
	
	/**
	  * Updates the created of the targeted OrganizationCompany instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any OrganizationCompany instance was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted OrganizationCompany instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any OrganizationCompany instance was affected
	  */
	def creatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the organizationId of the targeted OrganizationCompany instance(s)
	  * @param newOrganizationId A new organizationId to assign
	  * @return Whether any OrganizationCompany instance was affected
	  */
	def organizationIds_=(newOrganizationId: Int)(implicit connection: Connection) = 
		putColumn(model.organizationIdColumn, newOrganizationId)
}

