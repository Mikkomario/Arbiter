package vf.arbiter.command.model.error

/**
 * Thrown when data reading fails
 * @author Mikko Hilpinen
 * @since 20.10.2021, v0.2
 */
class ReadFailedException(message: String, cause: Throwable = null) extends Exception(message, cause)
