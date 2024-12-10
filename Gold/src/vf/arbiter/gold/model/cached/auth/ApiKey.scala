package vf.arbiter.gold.model.cached.auth

/**
 * A wrapper for an API-key used for accessing an API
 * @author Mikko Hilpinen
 * @since 15.9.2023, v1.4
 *
 * @param key Wrapped API key
 * @param paid Whether this key represents a paid plan
 */
case class ApiKey(key: String, paid: Boolean = false)
{
	override def toString = "API-key"
}
