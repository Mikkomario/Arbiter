package vf.arbiter.core.database.access.many.company

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.NonDeprecatedView
import utopia.vault.sql.SqlExtensions._
import vf.arbiter.core.model.stored.company.CompanyDetails

/**
  * The root access point when targeting multiple CompanyDetails at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbManyCompanyDetails extends ManyCompanyDetailsAccess with NonDeprecatedView[CompanyDetails]
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted CompanyDetails
	  * @return An access point to CompanyDetails with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbCompanyDetailsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbCompanyDetailsSubset(targetIds: Set[Int]) extends ManyCompanyDetailsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

