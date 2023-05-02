package vf.arbiter.core.database.access.many.location

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple StreetAddresses at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbStreetAddresses extends ManyStreetAddressesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted StreetAddresses
	  * @return An access point to StreetAddresses with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbStreetAddressesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbStreetAddressesSubset(targetIds: Set[Int]) extends ManyStreetAddressesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

