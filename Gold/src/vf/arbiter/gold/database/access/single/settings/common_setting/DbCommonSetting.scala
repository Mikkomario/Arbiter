package vf.arbiter.gold.database.access.single.settings.common_setting

import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.arbiter.gold.database.factory.settings.CommonSettingFactory
import vf.arbiter.gold.database.model.settings.CommonSettingModel
import vf.arbiter.gold.model.partial.settings.CommonSettingData
import vf.arbiter.gold.model.stored.settings.CommonSetting

/**
  * Used for accessing individual common settings
  * @author Mikko Hilpinen
  * @since 15.09.2023, v1.4
  */
object DbCommonSetting extends SingleRowModelAccess[CommonSetting] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CommonSettingModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CommonSettingFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted common setting
	  * @return An access point to that common setting
	  */
	def apply(id: Int) = DbSingleCommonSetting(id)
	
	/**
	 * @param key Targeted setting key
	 * @return Access to that setting
	 */
	def apply(key: String) = filterDistinct(model.withKey(key).toCondition)
	/**
	 * @param key Targeted key
	 * @param value Value to assign for that key
	 * @param connection Implicit DB Connection
	 */
	def update(key: String, value: Value)(implicit connection: Connection): Unit = {
		// Attempts update first
		val updateSucceeded = apply(key).value = value
		// If there was no row to update, performs an insert
		if (!updateSucceeded)
			model.insert(CommonSettingData(key, value))
	}
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique common settings.
	  * @return An access point to the common setting that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueCommonSettingAccess(mergeCondition(condition))
}

