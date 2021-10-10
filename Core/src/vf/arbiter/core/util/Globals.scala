package vf.arbiter.core.util

import utopia.flow.async.ThreadPool
import utopia.vault.database.ConnectionPool

import scala.concurrent.ExecutionContext

/**
 * Used for accessing values that are used across this project
 * @author Mikko Hilpinen
 * @since 10.10.2021, v0.1
 */
object Globals
{
	/**
	 * Threadpool used by this project
	 */
	val threadPool = new ThreadPool("Arbiter")
	
	/**
	 * Execution context used in this project
	 */
	implicit val executionContext: ExecutionContext = threadPool.executionContext
	/**
	 * Connection pool used for establishing database connections in this project
	 */
	implicit val connectionPool: ConnectionPool = new ConnectionPool()
}
