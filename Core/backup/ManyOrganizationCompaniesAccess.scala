package vf.arbiter.core.database.access.many.company

import utopia.citadel.database.Tables
import utopia.citadel.database.factory.organization.MembershipFactory
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.sql.{Select, Where}
import vf.arbiter.core.database.factory.company.OrganizationCompanyFactory
import vf.arbiter.core.database.model.company.OrganizationCompanyModel
import vf.arbiter.core.model.stored.company.OrganizationCompany

/**
  * A common trait for access points which target multiple OrganizationCompanies at a time
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait ManyOrganizationCompaniesAccess extends ManyRowModelAccess[OrganizationCompany] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	 * Factory used for constructing database the interaction models
	 */
	protected def model = OrganizationCompanyModel
	
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
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	 * @param connection Implicit DB Connection
	 * @return Organization memberships associated with these links
	 */
	def memberships(implicit connection: Connection) =
	{
		val membershipTable = Tables.organizationMembership
		// Joins to organization to membership
		// Only selects active memberships
		MembershipFactory(connection(Select(membershipTable join Tables.organization join table, membershipTable) +
			Where(mergeCondition(MembershipFactory.nonDeprecatedCondition))))
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = OrganizationCompanyFactory
	
	override protected def defaultOrdering = None
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the companyId of the targeted OrganizationCompany instance(s)
	  * @param newCompanyId A new companyId to assign
	  * @return Whether any OrganizationCompany instance was affected
	  */
	def companyId_=(newCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.companyIdColumn, newCompanyId)
	
	/**
	  * Updates the organizationId of the targeted OrganizationCompany instance(s)
	  * @param newOrganizationId A new organizationId to assign
	  * @return Whether any OrganizationCompany instance was affected
	  */
	def organizationId_=(newOrganizationId: Int)(implicit connection: Connection) = 
		putColumn(model.organizationIdColumn, newOrganizationId)
}

