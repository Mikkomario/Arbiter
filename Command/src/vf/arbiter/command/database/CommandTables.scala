package vf.arbiter.command.database

import utopia.citadel.database.Tables
import utopia.vault.model.immutable.Table

/**
  * Used for accessing the database tables introduced in this project
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
object CommandTables
{
	// COMPUTED	--------------------
	
	/**
	  * Table that contains InvoiceForms (Stores information about invoice form locations)
	  */
	def invoiceForm = apply("invoice_form")
	
	
	// OTHER	--------------------
	
	private def apply(tableName: String): Table = Tables(tableName)
}

