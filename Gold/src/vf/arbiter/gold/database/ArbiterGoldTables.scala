package vf.arbiter.gold.database

import utopia.citadel.database.Tables
import utopia.vault.model.immutable.Table

/**
  * Used for accessing the database tables introduced in this project
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
object ArbiterGoldTables
{
	// COMPUTED	--------------------
	
	/**
	  * Table that contains metal prices (Documents a metal's (average) price on a specific date)
	  */
	def metalPrice = apply("metal_price")
	
	
	// OTHER	--------------------
	
	private def apply(tableName: String): Table = Tables(tableName)
}

