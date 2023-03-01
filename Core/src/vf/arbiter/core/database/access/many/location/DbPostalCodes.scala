package vf.arbiter.core.database.access.many.location

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.SqlExtensions._

/**
  * The root access point when targeting multiple PostalCodes at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbPostalCodes extends ManyPostalCodesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted PostalCodes
	  * @return An access point to PostalCodes with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbPostalCodesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbPostalCodesSubset(targetIds: Set[Int]) extends ManyPostalCodesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

