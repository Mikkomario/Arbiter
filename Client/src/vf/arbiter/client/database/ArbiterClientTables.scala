package vf.arbiter.client.database

import utopia.vault.model.immutable.Table

/**
  * Used for accessing the database tables introduced in this project
  * @author Mikko Hilpinen
  * @since 01.05.2023, v2.0
  */
object ArbiterClientTables
{
	// COMPUTED	--------------------
	
	/**
	  * Table that contains sessions (Represents a local user-session)
	  */
	def session = apply("session")
	
	
	// OTHER	--------------------
	
	private def apply(tableName: String): Table = {
		// TODO: Refer to a tables instance of your choice
		// If you're using the Citadel module, import utopia.citadel.database.Tables
		// Tables(tableName)
		???
	}
}

