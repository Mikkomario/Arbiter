package vf.arbiter.core.database.access.many.location

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple Counties at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbCounties extends ManyCountiesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted Counties
	  * @return An access point to Counties with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbCountiesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbCountiesSubset(targetIds: Set[Int]) extends ManyCountiesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

