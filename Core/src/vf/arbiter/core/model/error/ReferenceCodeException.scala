package vf.arbiter.core.model.error

/**
 * Thrown on illegal reference codes
 * @author Mikko Hilpinen
 * @since 11.10.2021, v0.1
 */
class ReferenceCodeException(message: String, cause: Throwable = null) extends Exception(message, cause)
