package vf.arbiter.gold.database.access.many.settings.common_setting

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple common settings at a time
  * @author Mikko Hilpinen
  * @since 15.09.2023, v1.4
  */
object DbCommonSettings extends ManyCommonSettingsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted common settings
	  * @return An access point to common settings with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbCommonSettingsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbCommonSettingsSubset(targetIds: Set[Int]) extends ManyCommonSettingsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

