package vf.arbiter.core.database.access.many.company

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

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
		
		override def accessCondition = Some(index in targetIds)
	}
}

