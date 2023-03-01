package vf.arbiter.command.database.model.environment

import java.time.Instant
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.command.database.factory.environment.DescriptionImportFactory
import vf.arbiter.command.model.partial.environment.DescriptionImportData
import vf.arbiter.command.model.stored.environment.DescriptionImport

import java.nio.file.Path

/**
  * Used for constructing DescriptionImportModel instances and for inserting DescriptionImports to the database
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
object DescriptionImportModel 
	extends DataInserter[DescriptionImportModel, DescriptionImport, DescriptionImportData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains DescriptionImport path
	  */
	val pathAttName = "Path"
	
	/**
	  * Name of the property that contains DescriptionImport created
	  */
	val createdAttName = "Created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains DescriptionImport path
	  */
	def pathColumn = table(pathAttName)
	
	/**
	  * Column that contains DescriptionImport created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = DescriptionImportFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: DescriptionImportData) = apply(None, Some(data.path), Some(data.created))
	
	override def complete(id: Value, data: DescriptionImportData) = DescriptionImport(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this file was read
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A DescriptionImport id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param path Path to the read file
	  * @return A model containing only the specified path
	  */
	def withPath(path: Path) = apply(path = Some(path))
}

/**
  * Used for interacting with DescriptionImports in the database
  * @param id DescriptionImport database id
  * @param path Path to the read file
  * @param created Time when this file was read
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
case class DescriptionImportModel(id: Option[Int] = None, path: Option[Path] = None,
                                  created: Option[Instant] = None)
	extends StorableWithFactory[DescriptionImport]
{
	// IMPLEMENTED	--------------------
	
	override def factory = DescriptionImportModel.factory
	
	override def valueProperties = 
	{
		import DescriptionImportModel._
		Vector("id" -> id, pathAttName -> path.map { _.toString }, createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created A new created
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param path A new path
	  * @return A new copy of this model with the specified path
	  */
	def withPath(path: Path) = copy(path = Some(path))
}

