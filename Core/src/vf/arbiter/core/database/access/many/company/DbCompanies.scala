package vf.arbiter.core.database.access.many.company

import utopia.citadel.database.Tables
import utopia.citadel.database.model.organization.MembershipModel
import utopia.vault.database.Connection
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.{Select, Where}
import vf.arbiter.core.database.model.company.OrganizationCompanyModel

/**
  * The root access point when targeting multiple Companies at a time
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object DbCompanies extends ManyCompaniesAccess with UnconditionalView
{
	private def organizationLinkModel = OrganizationCompanyModel
	
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
				Where(membershipModel.nonDeprecatedCondition && membershipModel.withUserId(userId).toCondition)))
	}
}
