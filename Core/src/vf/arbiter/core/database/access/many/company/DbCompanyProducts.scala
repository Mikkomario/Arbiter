package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.{NonDeprecatedView, ViewManyByIntIds}
import vf.arbiter.core.model.stored.company.CompanyProduct

/**
  * The root access point when targeting multiple CompanyProducts at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbCompanyProducts
	extends ManyCompanyProductsAccess with NonDeprecatedView[CompanyProduct] with ViewManyByIntIds[ManyCompanyProductsAccess]

