package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple Companies at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbCompanies extends ManyCompaniesAccess with UnconditionalView
{
	/**
	 * @return An access point to detailed copies of these companies
	 */
	def detailed = DbDetailedCompanies
}
