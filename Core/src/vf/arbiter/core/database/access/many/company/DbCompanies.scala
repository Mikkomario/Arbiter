package vf.arbiter.core.database.access.many.company

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.view.{SubView, UnconditionalView}
import utopia.vault.sql.SqlExtensions._

/**
  * The root access point when targeting multiple Companies at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbCompanies extends ManyCompaniesAccess with UnconditionalView
{
	// COMPUTED ---------------------------------
	
	/**
	 * @return An access point to detailed copies of these companies
	 */
	def detailed = DbDetailedCompanies
	
	
	// OTHER    ---------------------------------
	
	/**
	 * @param ids Targeted company ids
	 * @return An access point to those companies
	 */
	def apply(ids: Iterable[Int]) = new DbCompaniesByIds(ids)
	
	
	// NESTED   ---------------------------------
	
	class DbCompaniesByIds(targetIds: Iterable[Int]) extends ManyCompaniesAccess with SubView
	{
		override protected def parent = DbCompanies
		
		override def filterCondition = index in targetIds
	}
}
