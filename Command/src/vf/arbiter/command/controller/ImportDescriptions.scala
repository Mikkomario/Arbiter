package vf.arbiter.command.controller

import utopia.citadel.importer.controller.ReadDescriptions
import utopia.flow.parse.JsonParser
import utopia.flow.time.Now
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.FileExtensions._
import utopia.vault.database.Connection
import vf.arbiter.command.database.access.many.environment.DbDescriptionImports
import vf.arbiter.command.database.model.environment.DescriptionImportModel
import vf.arbiter.command.model.error.ReadFailedException
import vf.arbiter.command.model.partial.environment.DescriptionImportData

import java.nio.file.Path
import scala.util.{Failure, Success}

/**
 * Used for importing description data from separate files
 * @author Mikko Hilpinen
 * @since 20.10.2021, v0.2
 */
object ImportDescriptions
{
	private val dataDirectory: Path = "data/descriptions"
	
	/**
	 * Imports description data if it has been modified
	 * @param connection Implicit DB Connection
	 * @param jsonParser Implicit json parser
	 * @return Whether reading succeeded or failed
	 */
	def ifModified()(implicit connection: Connection, jsonParser: JsonParser) =
	{
		// Checks when the descriptions were updated the last time and compares it to existing data
		val lastReads = DbDescriptionImports.pull
		dataDirectory
			.iterateChildren {
				// Finds json files that were not read recently
				_.filter { _.fileType == "json" }
					.filter { p => lastReads.find { _.path == p }
						.forall { read => p.lastModified.toOption.forall { _ > read.created } }
				}.toVector
			}
			// Imports the descriptions from those files
			.flatMap { pathsToRead =>
				val results = pathsToRead.map { p => ReadDescriptions(p).map { _ => p } }
				val (failures, successes) = results.divideWith { _.toEither }
				// Records the successful reads
				val (newInserts, idsToUpdate) = successes.divideWith { path => lastReads.find { _.path == path } match
				{
					case Some(previousRead) => Right(previousRead.id)
					case None => Left(path)
				} }
				lazy val updateTime = Now.toInstant
				if (idsToUpdate.nonEmpty)
					DbDescriptionImports(idsToUpdate).created = updateTime
				if (newInserts.nonEmpty)
					DescriptionImportModel.insert(newInserts.map { path => DescriptionImportData(path, updateTime) })
				
				// Returns failure if any reads failed
				failures.headOption match
				{
					case Some(error) => Failure(if (failures.size == 1) error else
						new ReadFailedException(s"Failed to read ${failures.size} or ${
							pathsToRead.size} description files", error))
					case None => Success(())
				}
			}
	}
	
	/**
	 * Imports description data, whether it was modified or not
	 * @param connection Implicit DB Connection
	 * @param jsonParser Implicit Json parser
	 * @return Whether read succeeded or failed
	 */
	def all()(implicit connection: Connection, jsonParser: JsonParser) =
	{
		dataDirectory.iterateChildren { _.filter { _.fileType == "json" }.toVector }.flatMap { pathsToRead =>
			if (pathsToRead.isEmpty)
				Success(())
			else
			{
				// Imports the descriptions
				val results = pathsToRead.map { p => ReadDescriptions(p).map { _ => p } }
				val (failures, successes) = results.divided
				// Stores new status
				DbDescriptionImports.delete()
				if (successes.nonEmpty)
				{
					val updateTime = Now.toInstant
					DescriptionImportModel.insert(successes.map { DescriptionImportData(_, updateTime) })
				}
				failures.headOption match
				{
					case Some(error) => Failure(if (failures.size == 1) error else
						new ReadFailedException(s"Failed to import ${failures.size} of ${
							pathsToRead.size} description files", error))
					case None => Success(())
				}
			}
		}
	}
}
