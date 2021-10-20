package vf.arbiter.command.database.access.single.device

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.command.database.factory.device.InvoiceFormFactory
import vf.arbiter.command.database.model.device.InvoiceFormModel
import vf.arbiter.command.model.stored.device.InvoiceForm

/**
  * A common trait for access points that return individual and distinct InvoiceForms.
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
trait UniqueInvoiceFormAccess 
	extends SingleRowModelAccess[InvoiceForm] 
		with DistinctModelAccess[InvoiceForm, Option[InvoiceForm], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the user who uses this form. None if no instance (or value) was found.
	  */
	def ownerId(implicit connection: Connection) = pullColumn(model.ownerIdColumn).int
	
	/**
	  * Id of the language this form uses. None if no instance (or value) was found.
	  */
	def languageId(implicit connection: Connection) = pullColumn(model.languageIdColumn).int
	
	/**
	  * Id of the company for which this form is used (if used for a specific company). None if no instance (or value) was found.
	  */
	def companyId(implicit connection: Connection) = pullColumn(model.companyIdColumn).int
	
	/**
	  * Path to the form file in the local file system. None if no instance (or value) was found.
	  */
	def path(implicit connection: Connection) = pullColumn(model.pathColumn).string
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoiceFormModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceFormFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the companyId of the targeted InvoiceForm instance(s)
	  * @param newCompanyId A new companyId to assign
	  * @return Whether any InvoiceForm instance was affected
	  */
	def companyId_=(newCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.companyIdColumn, newCompanyId)
	
	/**
	  * Updates the languageId of the targeted InvoiceForm instance(s)
	  * @param newLanguageId A new languageId to assign
	  * @return Whether any InvoiceForm instance was affected
	  */
	def languageId_=(newLanguageId: Int)(implicit connection: Connection) = 
		putColumn(model.languageIdColumn, newLanguageId)
	
	/**
	  * Updates the ownerId of the targeted InvoiceForm instance(s)
	  * @param newOwnerId A new ownerId to assign
	  * @return Whether any InvoiceForm instance was affected
	  */
	def ownerId_=(newOwnerId: Int)(implicit connection: Connection) = putColumn(model.ownerIdColumn, 
		newOwnerId)
	
	/**
	  * Updates the path of the targeted InvoiceForm instance(s)
	  * @param newPath A new path to assign
	  * @return Whether any InvoiceForm instance was affected
	  */
	def path_=(newPath: String)(implicit connection: Connection) = putColumn(model.pathColumn, newPath)
}

