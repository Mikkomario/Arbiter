package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.model.stored.company.CompanyDetails

/**
  * The root access point when targeting multiple CompanyDetails at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbManyCompanyDetails extends ManyCompanyDetailsAccess with NonDeprecatedView[CompanyDetails]

