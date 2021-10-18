package vf.arbiter.core.database.access.single.company

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.database.factory.company.FullCompanyDetailsFactory
import vf.arbiter.core.model.stored.company.CompanyDetails

/**
  * An access point to individual CompanyDetails, based on their id
  * @since 2021-10-14
  */
case class DbSingleCompanyDetails(id: Int) 
	extends UniqueCompanyDetailsAccess with SingleIntIdModelAccess[CompanyDetails]
{
	/**
	 * @param connection Implicit DB Connection
	 * @return A version of these company details which includes full address information
	 */
	def full(implicit connection: Connection) = FullCompanyDetailsFactory.get(condition)
}
