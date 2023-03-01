package vf.arbiter.core.database.access.many.company

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.SqlExtensions._

/**
  * The root access point when targeting multiple OrganizationCompanies at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbOrganizationCompanies extends ManyOrganizationCompaniesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted OrganizationCompanies
	  * @return An access point to OrganizationCompanies with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbOrganizationCompaniesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbOrganizationCompaniesSubset(targetIds: Set[Int]) extends ManyOrganizationCompaniesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

