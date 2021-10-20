package vf.arbiter.command.database.access.single.environment

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.command.database.factory.environment.DescriptionImportFactory
import vf.arbiter.command.database.model.environment.DescriptionImportModel
import vf.arbiter.command.model.stored.environment.DescriptionImport

/**
  * Used for accessing individual DescriptionImports
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
object DbDescriptionImport extends SingleRowModelAccess[DescriptionImport] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = DescriptionImportModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DescriptionImportFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted DescriptionImport instance
	  * @return An access point to that DescriptionImport
	  */
	def apply(id: Int) = DbSingleDescriptionImport(id)
}

