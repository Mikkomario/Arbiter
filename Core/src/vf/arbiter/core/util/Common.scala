package vf.arbiter.core.util

import utopia.flow.async.context.ThreadPool
import utopia.flow.util.logging.{Logger, SysErrLogger}
import utopia.vault.database.ConnectionPool

import scala.concurrent.ExecutionContext

/**
 * Used for accessing values that are used across this project
 * @author Mikko Hilpinen
 * @since 10.10.2021, v0.1
 */
object Common
{
	/**
	 * The commonly used logging implementation
	 */
	implicit val log: Logger = SysErrLogger
	
	/**
	 * Threadpool used by this project
	 */
	private val threadPool = new ThreadPool("Arbiter")
	
	/**
	 * Execution context used in this project
	 */
	implicit val executionContext: ExecutionContext = threadPool.executionContext
	/**
	 * Connection pool used for establishing database connections in this project
	 */
	implicit val connectionPool: ConnectionPool = new ConnectionPool()
}
