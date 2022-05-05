package vf.arbiter.core.controller.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import utopia.flow.util.AutoClose._
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.FileExtensions._

import java.io.IOException
import java.nio.file.Path
import scala.util.{Failure, Success, Try}

/**
  * Used for filling form fields on a pdf file
  * @author Mikko Hilpinen
  * @since 12.2.2022, v1.2
  */
object FillPdfForm
{
	/**
	  * Fills a pdf form using the specified input
	  * @param formPath    Path where the pdf form resides
	  * @param fieldValues Values to put to the form
	  * @return Success or failure. On success, contains the generated filled pdf file path and a map of individual
	  *         value set failures, which may be empty.
	  */
	def apply(formPath: Path, fieldValues: Map[String, String]): Try[(Path, Map[String, Throwable])] =
	{
		val outputPath = formPath.withMappedFileName { formName =>
			val mayBeCamel = formName.drop(1).exists { _.isUpper }
			val separator = {
				if (formName.contains('-'))
					"-"
				else if (formName.contains('_'))
					"_"
				else if (mayBeCamel)
					""
				else
					"-"
			}
			val terminator = if (mayBeCamel || formName.headOption.forall { _.isUpper }) "Filled" else "filled"
			s"$formName$separator$terminator"
		}.unique
		apply(formPath, fieldValues, outputPath).map { failures => outputPath -> failures }
	}
	
	/**
	  * Fills a pdf form using the specified input
	  * @param formPath    Path where the pdf form resides
	  * @param fieldValues Values to put to the form
	  * @param outputPath  Path where the filled copy will be stored
	  * @return Success or failure. On success, contains a map of individual value set failures, which may be empty.
	  */
	def apply(formPath: Path, fieldValues: Map[String, String], outputPath: Path) =
	{
		Try {
			PDDocument.load(formPath.toFile).consume { document =>
				Option(document.getDocumentCatalog.getAcroForm)
					.toTry { new IOException(s"No form is accessible in $formPath") }
					.flatMap { form =>
						// Sets field values, catches errors
						val errors = fieldValues.flatMap { case (fieldName, value) =>
							Try {
								Option(form.getField(fieldName))
									.toTry { new NoSuchElementException(
										s"$formPath doesn't contain field named $fieldName") }
									.map { _.setValue(value) }
							}.flatten.failure.map { fieldName -> _ }
						}
						// Flattens the form so that it's no longer editable
						// TODO: Add this feature later when it is suitable
						// form.flatten()
						// Writes the new document
						document.save(outputPath.toFile)
						// Fails only if all writes failed
						if (fieldValues.nonEmpty && errors.size == fieldValues.size)
							Failure(errors.head._2)
						else
							Success(errors)
					}
			}
		}.flatten
	}
	
	/**
	 * Creates a flattened copy of a form
	 * @param path Path to the form file to flatten
	 * @param outputPath Path to the location the flattened file will be stored
	 * @return Success or failure
	 */
	def flatten(path: Path, outputPath: Path) = {
		Try {
			PDDocument.load(path.toFile).consume { document =>
				Option(document.getDocumentCatalog.getAcroForm)
					.toTry { new IOException(s"No form is accessible in $path") }
					.map { form =>
						// Flattens the form so that it's no longer editable
						form.flatten()
						// Writes the new document
						document.save(outputPath.toFile)
					}
			}
		}.flatten
	}
}
