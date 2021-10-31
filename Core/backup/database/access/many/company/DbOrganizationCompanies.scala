package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple OrganizationCompanies at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbOrganizationCompanies extends ManyOrganizationCompaniesAccess with UnconditionalView