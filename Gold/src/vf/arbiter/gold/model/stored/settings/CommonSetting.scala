package vf.arbiter.gold.model.stored.settings

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.gold.database.access.single.settings.common_setting.DbSingleCommonSetting
import vf.arbiter.gold.model.partial.settings.CommonSettingData

/**
  * Represents a common setting that has already been stored in the database
  * @param id id of this common setting in the database
  * @param data Wrapped common setting data
  * @author Mikko Hilpinen
  * @since 15.09.2023, v1.4
  */
case class CommonSetting(id: Int, data: CommonSettingData) extends StoredModelConvertible[CommonSettingData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this common setting in the database
	  */
	def access = DbSingleCommonSetting(id)
}

