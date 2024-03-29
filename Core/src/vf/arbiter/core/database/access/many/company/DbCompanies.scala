package vf.arbiter.core.database.access.many.company

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple Companies at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbCompanies extends ManyCompaniesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted Companies
	  * @return An access point to Companies with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbCompaniesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbCompaniesSubset(targetIds: Set[Int]) extends ManyCompaniesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

