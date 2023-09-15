package vf.arbiter.gold.model.cached.auth

/**
 * A wrapper for an API-key used for accessing an API
 * @author Mikko Hilpinen
 * @since 15.9.2023, v1.4
 */
case class ApiKey(key: String)
{
	override def toString = "API-key"
}
