package vf.arbiter.accounting.database.access.many.transaction.party_entry

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple party entries at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbPartyEntries extends ManyPartyEntriesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted party entries
	  * @return An access point to party entries with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbPartyEntriesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbPartyEntriesSubset(targetIds: Set[Int]) extends ManyPartyEntriesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

