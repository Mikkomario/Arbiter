package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.model.combined.company.DetailedCompany

/**
 * Used for accessing multiple detailed companies at a time
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
object DbDetailedCompanies extends ManyDetailedCompaniesAccess with NonDeprecatedView[DetailedCompany]
