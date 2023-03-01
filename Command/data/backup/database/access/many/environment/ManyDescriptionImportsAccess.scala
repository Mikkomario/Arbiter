package vf.arbiter.command.database.access.many.environment

import java.time.Instant
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import vf.arbiter.command.database.factory.environment.DescriptionImportFactory
import vf.arbiter.command.database.model.environment.DescriptionImportModel
import vf.arbiter.command.model.stored.environment.DescriptionImport

object ManyDescriptionImportsAccess
{
	// NESTED	--------------------
	
	private class ManyDescriptionImportsSubView(override val parent: ManyRowModelAccess[DescriptionImport], 
		override val filterCondition: Condition) 
		extends ManyDescriptionImportsAccess with SubView
}

/**
  * A common trait for access points which target multiple DescriptionImports at a time
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
trait ManyDescriptionImportsAccess extends ManyRowModelAccess[DescriptionImport] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * paths of the accessible DescriptionImports
	  */
	def paths(implicit connection: Connection) = pullColumn(model.pathColumn)
		.flatMap { value => value.string }
	
	/**
	  * createds of the accessible DescriptionImports
	  */
	def created(implicit connection: Connection) =
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = DescriptionImportModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DescriptionImportFactory
	
	override protected def defaultOrdering = None
	
	override def filter(additionalCondition: Condition): ManyDescriptionImportsAccess = 
		new ManyDescriptionImportsAccess.ManyDescriptionImportsSubView(this, additionalCondition)
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the created of the targeted DescriptionImport instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any DescriptionImport instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the path of the targeted DescriptionImport instance(s)
	  * @param newPath A new path to assign
	  * @return Whether any DescriptionImport instance was affected
	  */
	def path_=(newPath: String)(implicit connection: Connection) = putColumn(model.pathColumn, newPath)
}

