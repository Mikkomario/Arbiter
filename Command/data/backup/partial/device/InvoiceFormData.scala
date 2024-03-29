package vf.arbiter.command.model.partial.device

import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.generic.casting.ValueConversions._

import java.nio.file.Path

/**
  * Stores information about invoice form locations
  * @param ownerId Id of the user who uses this form
  * @param languageId Id of the language this form uses
  * @param companyId Id of the company for which this form is used (if used for a specific company)
  * @param path Path to the form file in the local file system
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
case class InvoiceFormData(ownerId: Int, languageId: Int, companyId: Option[Int] = None, path: Path)
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("owner_id" -> ownerId, "language_id" -> languageId, "company_id" -> companyId, 
			"path" -> path.toString))
}

