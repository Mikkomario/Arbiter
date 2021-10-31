package vf.arbiter.core.database.access.many.company

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.SqlExtensions._

/**
  * The root access point when targeting multiple Banks at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbBanks extends ManyBanksAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted Banks
	  * @return An access point to Banks with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbBanksSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbBanksSubset(targetIds: Set[Int]) extends ManyBanksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

