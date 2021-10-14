package vf.arbiter.core.database.access.single.company

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.core.model.stored.company.Company

/**
  * An access point to individual Companies, based on their id
  * @since 2021-10-14
  */
case class DbSingleCompany(id: Int) extends UniqueCompanyAccess with SingleIntIdModelAccess[Company]

