package vf.arbiter.gold.database.model.settings

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.gold.database.factory.settings.CommonSettingFactory
import vf.arbiter.gold.model.partial.settings.CommonSettingData
import vf.arbiter.gold.model.stored.settings.CommonSetting

import java.time.Instant

/**
  * Used for constructing CommonSettingModel instances and for inserting common settings to the database
  * @author Mikko Hilpinen
  * @since 15.09.2023, v1.4
  */
object CommonSettingModel extends DataInserter[CommonSettingModel, CommonSetting, CommonSettingData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains common setting key
	  */
	val keyAttName = "key"
	
	/**
	  * Name of the property that contains common setting value
	  */
	val valueAttName = "value"
	
	/**
	  * Name of the property that contains common setting last updated
	  */
	val lastUpdatedAttName = "lastUpdated"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains common setting key
	  */
	def keyColumn = table(keyAttName)
	
	/**
	  * Column that contains common setting value
	  */
	def valueColumn = table(valueAttName)
	
	/**
	  * Column that contains common setting last updated
	  */
	def lastUpdatedColumn = table(lastUpdatedAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = CommonSettingFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: CommonSettingData) = apply(None, data.key, data.value, Some(data.lastUpdated))
	
	override protected def complete(id: Value, data: CommonSettingData) = CommonSetting(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param id A common setting id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param key Key that represents this setting's target / function
	  * @return A model containing only the specified key
	  */
	def withKey(key: String) = apply(key = key)
	
	/**
	  * @param lastUpdated Time when this setting was last modified
	  * @return A model containing only the specified last updated
	  */
	def withLastUpdated(lastUpdated: Instant) = apply(lastUpdated = Some(lastUpdated))
	
	/**
	  * @param value Value given for this setting
	  * @return A model containing only the specified value
	  */
	def withValue(value: Value) = apply(value = value)
}

/**
  * Used for interacting with CommonSettings in the database
  * @param id common setting database id
  * @author Mikko Hilpinen
  * @since 15.09.2023, v1.4
  */
case class CommonSettingModel(id: Option[Int] = None, key: String = "", value: Value = Value.empty, 
	lastUpdated: Option[Instant] = None) 
	extends StorableWithFactory[CommonSetting]
{
	// IMPLEMENTED	--------------------
	
	override def factory = CommonSettingModel.factory
	
	override def valueProperties = {
		import CommonSettingModel._
		Vector("id" -> id, keyAttName -> key, valueAttName -> value.mapIfNotEmpty { _.toJson },
			lastUpdatedAttName -> lastUpdated)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param key Key that represents this setting's target / function
	  * @return A new copy of this model with the specified key
	  */
	def withKey(key: String) = copy(key = key)
	
	/**
	  * @param lastUpdated Time when this setting was last modified
	  * @return A new copy of this model with the specified last updated
	  */
	def withLastUpdated(lastUpdated: Instant) = copy(lastUpdated = Some(lastUpdated))
	
	/**
	  * @param value Value given for this setting
	  * @return A new copy of this model with the specified value
	  */
	def withValue(value: Value) = copy(value = value)
}

