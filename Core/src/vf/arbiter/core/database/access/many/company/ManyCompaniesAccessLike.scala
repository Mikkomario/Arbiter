package vf.arbiter.core.database.access.many.company

import utopia.citadel.database.CitadelTables
import utopia.citadel.database.model.organization.MembershipModel
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.{Select, Where}
import vf.arbiter.core.database.model.company.{CompanyModel, OrganizationCompanyModel}

import java.time.Instant

/**
  * A common trait for access points which target multiple Companies or company-like instances at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
trait ManyCompaniesAccessLike[+A, +Repr] extends ManyRowModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	 * @return Model used for interacting with company-organization links
	 */
	protected def organizationLinkModel = OrganizationCompanyModel
	
	/**
	  * yCodes of the accessible Companies
	  */
	def yCodes(implicit connection: Connection) = pullColumn(companyModel.yCodeColumn)
		.flatMap { value => value.string }
	/**
	  * creatorIds of the accessible Companies
	  */
	def creatorIds(implicit connection: Connection) =
		pullColumn(companyModel.creatorIdColumn).flatMap { value => value.int }
	/**
	  * createds of the accessible Companies
	  */
	def createds(implicit connection: Connection) =
		pullColumn(companyModel.createdColumn).flatMap { value => value.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def companyModel = CompanyModel
	
	
	// OTHER	--------------------
	
	/**
	 * @param yCodes A set of y-codes to target
	 * @return A copy of this access point that only targets companies with those codes
	 */
	def withAnyOfYCodes(yCodes: Iterable[String]) = filter(companyModel.yCodeColumn in yCodes)
	
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
			Select.tables(target join organizationLinkModel.table join CitadelTables.organization join membershipModel.table,
				factory.tables) + Where(mergeCondition(membershipModel.nonDeprecatedCondition &&
				membershipModel.withUserId(userId).toCondition))))
	}
	
	/**
	  * Updates the created of the targeted Company instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any Company instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) =
		putColumn(companyModel.createdColumn, newCreated)
	/**
	  * Updates the creatorId of the targeted Company instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any Company instance was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) =
		putColumn(companyModel.creatorIdColumn, newCreatorId)
	/**
	  * Updates the yCode of the targeted Company instance(s)
	  * @param newYCode A new yCode to assign
	  * @return Whether any Company instance was affected
	  */
	def yCode_=(newYCode: String)(implicit connection: Connection) = putColumn(companyModel.yCodeColumn, newYCode)
}

