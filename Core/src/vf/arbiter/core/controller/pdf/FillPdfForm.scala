package vf.arbiter.core.controller.pdf

import com.itextpdf.forms.PdfAcroForm
import com.itextpdf.kernel.pdf.{PdfDocument, PdfReader, PdfWriter}
import utopia.flow.util.AutoClose._
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.FileExtensions._

import java.nio.file.Path
import scala.util.{Failure, Success, Try}

/**
  * Used for filling form fields on a pdf file
  * @author Mikko Hilpinen
  * @since 8.10.2021, v0.1
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
			new PdfReader(formPath.toFile).consume { reader =>
				new PdfWriter(outputPath.toFile).consume { writer =>
					new PdfDocument(reader, writer).consume { document =>
						val form = PdfAcroForm.getAcroForm(document, true)
						// Updates the named fields based on input
						// Caches failures on individual value sets
						val setFailures = fieldValues.flatMap { case (fieldName, fieldValue) =>
							// Position ordering leftX, lowerY, rightX, upperY
							// form.getField(fieldName).getWidgets.get(0).getRectangle.getAsNumber(1)
							// TODO: Use this kind of a rectangle to set image position & size accordingly
							// See: https://www.concretepage.com/itext/add-image-in-pdf-using-itext-in-java
							Try { form.getField(fieldName).setValue(fieldValue) }.failure.map { fieldName -> _ }
						}
						// Only fails if all value sets failed, otherwise returns a list of failures
						if (setFailures.nonEmpty && setFailures.size == fieldValues.size)
							Failure(setFailures.head._2)
						else
							Success(setFailures)
					}
				}
			}
		}.flatten
	}
}
