package vf.arbiter.core.database.access.many.company

import utopia.citadel.database.Tables
import utopia.citadel.database.model.organization.MembershipModel

import java.time.Instant
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.{Condition, Select, Where}
import vf.arbiter.core.database.factory.company.CompanyFactory
import vf.arbiter.core.database.model.company.{CompanyModel, OrganizationCompanyModel}
import vf.arbiter.core.model.stored.company.Company

object ManyCompaniesAccess
{
	// NESTED	--------------------
	
	private class ManyCompaniesSubView(override val parent: ManyRowModelAccess[Company], 
		override val filterCondition: Condition) 
		extends ManyCompaniesAccess with SubView
}

/**
  * A common trait for access points which target multiple Companies at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
trait ManyCompaniesAccess extends ManyRowModelAccess[Company] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	 * @return Model used for interacting with company-organization links
	 */
	protected def organizationLinkModel = OrganizationCompanyModel
	
	/**
	  * yCodes of the accessible Companies
	  */
	def yCodes(implicit connection: Connection) = pullColumn(model.yCodeColumn)
		.flatMap { value => value.string }
	
	/**
	  * creatorIds of the accessible Companies
	  */
	def creatorIds(implicit connection: Connection) = 
		pullColumn(model.creatorIdColumn).flatMap { value => value.int }
	
	/**
	  * createds of the accessible Companies
	  */
	def createds(implicit connection: Connection) = 
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyFactory
	
	override protected def defaultOrdering = Some(factory.defaultOrdering)
	
	override def filter(additionalCondition: Condition): ManyCompaniesAccess = 
		new ManyCompaniesAccess.ManyCompaniesSubView(this, additionalCondition)
	
	
	// OTHER	--------------------
	
	/**
	 * @param userId A user id
	 * @param connection Implicit DB Connection
	 * @return All companies that are linked with an organization that user belongs to
	 */
	def linkedWithUserWithId(userId: Int)(implicit connection: Connection) =
	{
		val membershipModel = MembershipModel
		// Joins to organization link -> organization -> membership
		factory(connection(
			Select(table join organizationLinkModel.table join Tables.organization join membershipModel.table, table) +
				Where(mergeCondition(membershipModel.nonDeprecatedCondition &&
					membershipModel.withUserId(userId).toCondition))))
	}
	
	/**
	  * Updates the created of the targeted Company instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any Company instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted Company instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any Company instance was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the yCode of the targeted Company instance(s)
	  * @param newYCode A new yCode to assign
	  * @return Whether any Company instance was affected
	  */
	def yCode_=(newYCode: String)(implicit connection: Connection) = putColumn(model.yCodeColumn, newYCode)
}

