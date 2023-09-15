package vf.arbiter.gold.database.access.single.settings.common_setting

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.gold.model.stored.settings.CommonSetting

/**
  * An access point to individual common settings, based on their id
  * @author Mikko Hilpinen
  * @since 15.09.2023, v1.4
  */
case class DbSingleCommonSetting(id: Int) 
	extends UniqueCommonSettingAccess with SingleIntIdModelAccess[CommonSetting]

