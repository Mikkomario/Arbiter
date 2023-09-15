package vf.arbiter.gold.database.access.single.settings.common_setting

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleChronoRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.sql.Condition
import vf.arbiter.gold.database.factory.settings.CommonSettingFactory
import vf.arbiter.gold.database.model.settings.CommonSettingModel
import vf.arbiter.gold.model.stored.settings.CommonSetting

import java.time.Instant

object UniqueCommonSettingAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueCommonSettingAccess = new _UniqueCommonSettingAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueCommonSettingAccess(condition: Condition) extends UniqueCommonSettingAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct common settings.
  * @author Mikko Hilpinen
  * @since 15.09.2023, v1.4
  */
trait UniqueCommonSettingAccess 
	extends SingleChronoRowModelAccess[CommonSetting, UniqueCommonSettingAccess] 
		with DistinctModelAccess[CommonSetting, Option[CommonSetting], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Key that represents this setting's target / function. None if no common setting (or value) was found.
	  */
	def key(implicit connection: Connection) = pullColumn(model.keyColumn).getString
	
	/**
	  * Value given for this setting. None if no common setting (or value) was found.
	  */
	def value(implicit connection: Connection) =
		pullColumn(model.valueColumn).mapIfNotEmpty { v => JsonBunny.sureMunch(v.getString) }
	
	/**
	  * Time when this setting was last modified. None if no common setting (or value) was found.
	  */
	def lastUpdated(implicit connection: Connection) = pullColumn(model.lastUpdatedColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CommonSettingModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CommonSettingFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueCommonSettingAccess = 
		new UniqueCommonSettingAccess._UniqueCommonSettingAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the keys of the targeted common settings
	  * @param newKey A new key to assign
	  * @return Whether any common setting was affected
	  */
	def key_=(newKey: String)(implicit connection: Connection) = putColumn(model.keyColumn, newKey)
	
	/**
	  * Updates the last update times of the targeted common settings
	  * @param newLastUpdated A new last updated to assign
	  * @return Whether any common setting was affected
	  */
	def lastUpdated_=(newLastUpdated: Instant)(implicit connection: Connection) = 
		putColumn(model.lastUpdatedColumn, newLastUpdated)
	
	/**
	  * Updates the values of the targeted common settings
	  * @param newValue A new value to assign
	  * @return Whether any common setting was affected
	  */
	def value_=(newValue: Value)(implicit connection: Connection) =
		putColumn(model.valueColumn, newValue.toJson)
}

