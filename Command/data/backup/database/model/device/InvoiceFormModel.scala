package vf.arbiter.command.database.model.device

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.command.database.factory.device.InvoiceFormFactory
import vf.arbiter.command.model.partial.device.InvoiceFormData
import vf.arbiter.command.model.stored.device.InvoiceForm

import java.nio.file.Path

/**
  * Used for constructing InvoiceFormModel instances and for inserting InvoiceForms to the database
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
object InvoiceFormModel extends DataInserter[InvoiceFormModel, InvoiceForm, InvoiceFormData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains InvoiceForm ownerId
	  */
	val ownerIdAttName = "OwnerId"
	
	/**
	  * Name of the property that contains InvoiceForm languageId
	  */
	val languageIdAttName = "LanguageId"
	
	/**
	  * Name of the property that contains InvoiceForm companyId
	  */
	val companyIdAttName = "CompanyId"
	
	/**
	  * Name of the property that contains InvoiceForm path
	  */
	val pathAttName = "Path"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains InvoiceForm ownerId
	  */
	def ownerIdColumn = table(ownerIdAttName)
	
	/**
	  * Column that contains InvoiceForm languageId
	  */
	def languageIdColumn = table(languageIdAttName)
	
	/**
	  * Column that contains InvoiceForm companyId
	  */
	def companyIdColumn = table(companyIdAttName)
	
	/**
	  * Column that contains InvoiceForm path
	  */
	def pathColumn = table(pathAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = InvoiceFormFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: InvoiceFormData) = 
		apply(None, Some(data.ownerId), Some(data.languageId), data.companyId, Some(data.path))
	
	override def complete(id: Value, data: InvoiceFormData) = InvoiceForm(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param companyId Id of the company for which this form is used (if used for a specific company)
	  * @return A model containing only the specified companyId
	  */
	def withCompanyId(companyId: Int) = apply(companyId = Some(companyId))
	
	/**
	  * @param id A InvoiceForm id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param languageId Id of the language this form uses
	  * @return A model containing only the specified languageId
	  */
	def withLanguageId(languageId: Int) = apply(languageId = Some(languageId))
	
	/**
	  * @param ownerId Id of the user who uses this form
	  * @return A model containing only the specified ownerId
	  */
	def withOwnerId(ownerId: Int) = apply(ownerId = Some(ownerId))
	
	/**
	  * @param path Path to the form file in the local file system
	  * @return A model containing only the specified path
	  */
	def withPath(path: Path) = apply(path = Some(path))
}

/**
  * Used for interacting with InvoiceForms in the database
  * @param id InvoiceForm database id
  * @param ownerId Id of the user who uses this form
  * @param languageId Id of the language this form uses
  * @param companyId Id of the company for which this form is used (if used for a specific company)
  * @param path Path to the form file in the local file system
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
case class InvoiceFormModel(id: Option[Int] = None, ownerId: Option[Int] = None, 
	languageId: Option[Int] = None, companyId: Option[Int] = None, path: Option[Path] = None)
	extends StorableWithFactory[InvoiceForm]
{
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceFormModel.factory
	
	override def valueProperties = 
	{
		import InvoiceFormModel._
		Vector("id" -> id, ownerIdAttName -> ownerId, languageIdAttName -> languageId, 
			companyIdAttName -> companyId, pathAttName -> path.map { _.toString })
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param companyId A new companyId
	  * @return A new copy of this model with the specified companyId
	  */
	def withCompanyId(companyId: Int) = copy(companyId = Some(companyId))
	
	/**
	  * @param languageId A new languageId
	  * @return A new copy of this model with the specified languageId
	  */
	def withLanguageId(languageId: Int) = copy(languageId = Some(languageId))
	
	/**
	  * @param ownerId A new ownerId
	  * @return A new copy of this model with the specified ownerId
	  */
	def withOwnerId(ownerId: Int) = copy(ownerId = Some(ownerId))
	
	/**
	  * @param path A new path
	  * @return A new copy of this model with the specified path
	  */
	def withPath(path: Path) = copy(path = Some(path))
}

