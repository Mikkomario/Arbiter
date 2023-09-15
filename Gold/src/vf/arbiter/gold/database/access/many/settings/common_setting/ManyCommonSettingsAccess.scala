package vf.arbiter.gold.database.access.many.settings.common_setting

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.ChronoRowFactoryView
import utopia.vault.sql.Condition
import vf.arbiter.gold.database.factory.settings.CommonSettingFactory
import vf.arbiter.gold.database.model.settings.CommonSettingModel
import vf.arbiter.gold.model.stored.settings.CommonSetting

import java.time.Instant

object ManyCommonSettingsAccess
{
	// NESTED	--------------------
	
	private class ManyCommonSettingsSubView(condition: Condition) extends ManyCommonSettingsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple common settings at a time
  * @author Mikko Hilpinen
  * @since 15.09.2023, v1.4
  */
trait ManyCommonSettingsAccess 
	extends ManyRowModelAccess[CommonSetting] 
		with ChronoRowFactoryView[CommonSetting, ManyCommonSettingsAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * keys of the accessible common settings
	  */
	def keys(implicit connection: Connection) = pullColumn(model.keyColumn).flatMap { _.string }
	
	/**
	  * values of the accessible common settings
	  */
	def values(implicit connection: Connection) = 
		pullColumn(model.valueColumn).map { v => v.mapIfNotEmpty { v => JsonBunny.sureMunch(v.getString) } }
	
	/**
	  * last update times of the accessible common settings
	  */
	def lastUpdateTimes(implicit connection: Connection) = 
		pullColumn(model.lastUpdatedColumn).map { v => v.getInstant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CommonSettingModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CommonSettingFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyCommonSettingsAccess = 
		new ManyCommonSettingsAccess.ManyCommonSettingsSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the keys of the targeted common settings
	  * @param newKey A new key to assign
	  * @return Whether any common setting was affected
	  */
	def keys_=(newKey: String)(implicit connection: Connection) = putColumn(model.keyColumn, newKey)
	
	/**
	  * Updates the last update times of the targeted common settings
	  * @param newLastUpdated A new last updated to assign
	  * @return Whether any common setting was affected
	  */
	def lastUpdateTimes_=(newLastUpdated: Instant)(implicit connection: Connection) = 
		putColumn(model.lastUpdatedColumn, newLastUpdated)
	
	/**
	  * Updates the values of the targeted common settings
	  * @param newValue A new value to assign
	  * @return Whether any common setting was affected
	  */
	def values_=(newValue: Value)(implicit connection: Connection) = putColumn(model.valueColumn, 
		newValue.toJson)
}

