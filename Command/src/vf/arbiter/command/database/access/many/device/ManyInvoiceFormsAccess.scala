package vf.arbiter.command.database.access.many.device

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import vf.arbiter.command.database.factory.device.InvoiceFormFactory
import vf.arbiter.command.database.model.device.InvoiceFormModel
import vf.arbiter.command.model.stored.device.InvoiceForm

object ManyInvoiceFormsAccess
{
	// NESTED	--------------------
	
	private class ManyInvoiceFormsSubView(override val parent: ManyRowModelAccess[InvoiceForm], 
		override val filterCondition: Condition) 
		extends ManyInvoiceFormsAccess with SubView
}

/**
  * A common trait for access points which target multiple InvoiceForms at a time
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
trait ManyInvoiceFormsAccess extends ManyRowModelAccess[InvoiceForm] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * ownerIds of the accessible InvoiceForms
	  */
	def ownerIds(implicit connection: Connection) = pullColumn(model.ownerIdColumn)
		.flatMap { value => value.int }
	/**
	  * languageIds of the accessible InvoiceForms
	  */
	def languageIds(implicit connection: Connection) = 
		pullColumn(model.languageIdColumn).flatMap { value => value.int }
	/**
	  * companyIds of the accessible InvoiceForms
	  */
	def companyIds(implicit connection: Connection) = 
		pullColumn(model.companyIdColumn).flatMap { value => value.int }
	/**
	  * paths of the accessible InvoiceForms
	  */
	def paths(implicit connection: Connection) = pullColumn(model.pathColumn)
		.flatMap { value => value.string }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoiceFormModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceFormFactory
	
	override protected def defaultOrdering = None
	
	override def filter(additionalCondition: Condition): ManyInvoiceFormsAccess = 
		new ManyInvoiceFormsAccess.ManyInvoiceFormsSubView(this, additionalCondition)
	
	
	// OTHER	--------------------
	
	/**
	 * @param userId Id of the user who's using the forms
	 * @return An access point to forms used by that user
	 */
	def forUserWithId(userId: Int) = filter(model.withOwnerId(userId).toCondition)
	/**
	 * @param languageId Id of the language used in the forms
	 * @return An access point to forms using that language
	 */
	def forLanguageWithId(languageId: Int) = filter(model.withLanguageId(languageId).toCondition)
	
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

