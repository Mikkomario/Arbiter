package vf.arbiter.command.model.partial.environment

import java.time.Instant
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now

import java.nio.file.Path

/**
  * Lists times when different description files were last read
  * @param path Path to the read file
  * @param created Time when this file was read
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
case class DescriptionImportData(path: Path, created: Instant = Now) extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("path" -> path.toString, "created" -> created))
}

