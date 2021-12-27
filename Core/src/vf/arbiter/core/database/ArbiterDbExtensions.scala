package vf.arbiter.core.database

import utopia.citadel.database.CitadelTables
import utopia.citadel.database.access.single.user.DbSingleUser
import utopia.citadel.database.model.organization.MembershipModel
import utopia.vault.database.Connection
import utopia.vault.sql.Exists
import vf.arbiter.core.database.model.company.OrganizationCompanyModel

/**
 * Database extensions introduced in this project
 * @author Mikko Hilpinen
 * @since 27.12.2021, v1.2
 */
object ArbiterDbExtensions
{
	implicit class DbCompanyUser(val u: DbSingleUser) extends AnyVal
	{
		/**
		 * @param companyId  Id of targeted company
		 * @param connection Implicit DB Connection
		 * @return Whether this user is a member in that company
		 */
		def isMemberOfCompanyWithId(companyId: Int)(implicit connection: Connection) =
		{
			// Joins membership <-> organization <-> organization company link
			Exists(CitadelTables.membership join CitadelTables.organization join CoreTables.organizationCompany,
				MembershipModel.withUserId(u.id).toCondition &&
					OrganizationCompanyModel.withCompanyId(companyId).toCondition)
		}
	}
}
