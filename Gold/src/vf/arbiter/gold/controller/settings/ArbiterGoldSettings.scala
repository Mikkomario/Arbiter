package vf.arbiter.gold.controller.settings

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import vf.arbiter.gold.database.access.single.settings.common_setting.DbCommonSetting
import vf.arbiter.gold.model.cached.auth.ApiKey

/**
 * An interface used for accessing settings concerning gold-related features
 * @author Mikko Hilpinen
 * @since 14.9.2023, v1.4
 */
object ArbiterGoldSettings
{
	/**
	 * @param connection Implicit DB Connection
	 * @return API-key that's currently specified in the settings.
	 *         None if no API-key is specified at this time.
	 */
	def apiKey(implicit connection: Connection) =
		apply("metal-price-api-key").string.map { ApiKey(_, paidPlan) }
	def apiKey_=(newKey: String)(implicit connection: Connection): Unit = update("metal-price-api-key", newKey)
	def apiKey_=(newKey: ApiKey)(implicit connection: Connection): Unit = {
		apiKey = newKey.key
		paidPlan = newKey.paid
	}
	
	/**
	 * @param connection Implicit DB connection
	 * @return Whether the user has a paid metal price API plan
	 */
	def paidPlan(implicit connection: Connection) = apply("metal-price-api-paid-plan").getBoolean
	def paidPlan_=(isPaid: Boolean)(implicit connection: Connection) = update("metal-price-api-paid-plan", isPaid)
	
	private def apply(key: String)(implicit connection: Connection) = DbCommonSetting(key).value
	private def update(key: String, value: Value)(implicit connection: Connection) = DbCommonSetting(key) = value
}
