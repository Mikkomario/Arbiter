package vf.arbiter.command.controller

import utopia.flow.util.FileExtensions._
import utopia.trove.event.DatabaseSetupEvent.{DatabaseConfigured, DatabaseStarted, SetupFailed, SetupSucceeded, UpdateApplied, UpdateFailed, UpdatesFound}
import utopia.trove.event.{DatabaseSetupEvent, DatabaseSetupListener}

/**
 * Used for listening to and reacting to database setup events
 * @author Mikko Hilpinen
 * @since 20.10.2021, v0.2
 */
class ArbiterDbSetupListener extends DatabaseSetupListener
{
	private var _failed = false
	private var _updated = false
	
	/**
	 * @return Whether the setup failed at some point
	 */
	def failed = _failed
	/**
	 * @return Whether the database structure was updated
	 */
	def updated = _updated
	
	override def onDatabaseSetupEvent(event: DatabaseSetupEvent) = event match
	{
		case DatabaseConfigured => println("Database configured. Starting...")
		case DatabaseStarted => println("Database started!")
		case UpdatesFound(filesToImport, originVersion) =>
			originVersion match {
				case Some(origin) => println(s"Found ${filesToImport.size} updates over current version $origin:")
				case None => println(s"Starting with ${filesToImport.size} initial updates:")
			}
			filesToImport.foreach { f => println(s"- ${f.path.fileName}") }
			println("Starting the update(s)...")
		case UpdateApplied(appliedUpdate, remainingUpdates) =>
			_updated = true
			val remainingPart = if (remainingUpdates.isEmpty) "" else s" ${remainingUpdates.size} updates remaining..."
			println(s"Updated to version ${appliedUpdate.targetVersion}.$remainingPart")
		case SetupFailed(error) =>
			error.printStackTrace()
			println(s"Failed to setup the database due to the error above (${error.getMessage})")
			println("Please contact the developer if you need any help resolving this issue")
			_failed = true
		case UpdateFailed(error, update, currentVersion) =>
			error.printStackTrace()
			println(s"Failed to update to version ${update.targetVersion} due to an error: ${error.getMessage}")
			println("Please contact the developer and inform them about this problem")
			currentVersion match {
				case Some(version) =>
					println(s"Continuing with version $version. This may lead to unexpected problems, however.")
				case None => _failed = true
			}
		case SetupSucceeded(version) =>
			version match
			{
				case Some(version) => println(s"The database is ready to be used. Current version is ${version.number}")
				case None =>
					println("Database structure was not updated. " +
						"Please make sure you include the database structure documents correctly.")
					_failed = true
			}
	}
}
