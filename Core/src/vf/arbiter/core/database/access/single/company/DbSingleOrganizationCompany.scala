package vf.arbiter.core.database.access.single.company

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.model.stored.company.OrganizationCompany

/**
  * An access point to individual OrganizationCompanies, based on their id
  * @since 2021-10-14
  */
case class DbSingleOrganizationCompany(id: Int) 
	extends UniqueOrganizationCompanyAccess with SingleIntIdModelAccess[OrganizationCompany]

