package vf.arbiter.gold.database.factory.settings

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.gold.database.ArbiterGoldTables
import vf.arbiter.gold.model.partial.settings.CommonSettingData
import vf.arbiter.gold.model.stored.settings.CommonSetting

/**
  * Used for reading common setting data from the DB
  * @author Mikko Hilpinen
  * @since 15.09.2023, v1.4
  */
object CommonSettingFactory 
	extends FromValidatedRowModelFactory[CommonSetting] with FromRowFactoryWithTimestamps[CommonSetting]
{
	// IMPLEMENTED	--------------------
	
	override def creationTimePropertyName = "lastUpdated"
	
	override def table = ArbiterGoldTables.commonSetting
	
	override protected def fromValidatedModel(valid: Model) = 
		CommonSetting(valid("id").getInt, CommonSettingData(valid("key").getString, 
			valid("value").mapIfNotEmpty { v => JsonBunny.sureMunch(v.getString) }, 
			valid("lastUpdated").getInstant))
}

