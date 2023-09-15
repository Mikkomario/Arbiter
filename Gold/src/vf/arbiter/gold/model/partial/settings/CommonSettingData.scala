package vf.arbiter.gold.model.partial.settings

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration, Value}
import utopia.flow.generic.model.mutable.DataType.AnyType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object CommonSettingData extends FromModelFactoryWithSchema[CommonSettingData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("key", StringType), PropertyDeclaration("value", AnyType, 
			isOptional = true), PropertyDeclaration("lastUpdated", InstantType, Vector("last_updated"), 
			isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		CommonSettingData(valid("key").getString, valid("value"), valid("lastUpdated").getInstant)
}

/**
  * Represents a single (mutable) setting key-value pair used in common configurations
  * @param key Key that represents this setting's target / function
  * @param value Value given for this setting
  * @param lastUpdated Time when this setting was last modified
  * @author Mikko Hilpinen
  * @since 15.09.2023, v1.4
  */
case class CommonSettingData(key: String, value: Value = Value.empty, lastUpdated: Instant = Now) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("key" -> key, "value" -> value, "lastUpdated" -> lastUpdated))
}

