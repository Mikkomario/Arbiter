package vf.arbiter.core.database.access.many.invoice

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.invoice.InvoiceItemFactory
import vf.arbiter.core.database.model.invoice.InvoiceItemModel
import vf.arbiter.core.model.stored.invoice.InvoiceItem

/**
  * A common trait for access points which target multiple InvoiceItems at a time
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait ManyInvoiceItemsAccess extends ManyRowModelAccess[InvoiceItem] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * invoiceIds of the accessible InvoiceItems
	  */
	def invoiceIds(implicit connection: Connection) = 
		pullColumn(model.invoiceIdColumn).flatMap { value => value.int }
	
	/**
	  * descriptions of the accessible InvoiceItems
	  */
	def descriptions(implicit connection: Connection) = 
		pullColumn(model.descriptionColumn).flatMap { value => value.string }
	
	/**
	  * amounts of the accessible InvoiceItems
	  */
	def amounts(implicit connection: Connection) = 
		pullColumn(model.amountColumn).flatMap { value => value.double }
	
	/**
	  * unitIds of the accessible InvoiceItems
	  */
	def unitIds(implicit connection: Connection) = pullColumn(model.unitIdColumn)
		.flatMap { value => value.int }
	
	/**
	  * pricePerUnits of the accessible InvoiceItems
	  */
	def pricePerUnits(implicit connection: Connection) = 
		pullColumn(model.pricePerUnitColumn).flatMap { value => value.double }
	
	/**
	  * taxModifiers of the accessible InvoiceItems
	  */
	def taxModifiers(implicit connection: Connection) = 
		pullColumn(model.taxModifierColumn).flatMap { value => value.double }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoiceItemModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceItemFactory
	
	override protected def defaultOrdering = None
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the amount of the targeted InvoiceItem instance(s)
	  * @param newAmount A new amount to assign
	  * @return Whether any InvoiceItem instance was affected
	  */
	def amount_=(newAmount: Double)(implicit connection: Connection) = putColumn(model.amountColumn, 
		newAmount)
	
	/**
	  * Updates the description of the targeted InvoiceItem instance(s)
	  * @param newDescription A new description to assign
	  * @return Whether any InvoiceItem instance was affected
	  */
	def description_=(newDescription: String)(implicit connection: Connection) = 
		putColumn(model.descriptionColumn, newDescription)
	
	/**
	  * Updates the invoiceId of the targeted InvoiceItem instance(s)
	  * @param newInvoiceId A new invoiceId to assign
	  * @return Whether any InvoiceItem instance was affected
	  */
	def invoiceId_=(newInvoiceId: Int)(implicit connection: Connection) = 
		putColumn(model.invoiceIdColumn, newInvoiceId)
	
	/**
	  * Updates the pricePerUnit of the targeted InvoiceItem instance(s)
	  * @param newPricePerUnit A new pricePerUnit to assign
	  * @return Whether any InvoiceItem instance was affected
	  */
	def pricePerUnit_=(newPricePerUnit: Double)(implicit connection: Connection) = 
		putColumn(model.pricePerUnitColumn, newPricePerUnit)
	
	/**
	  * Updates the taxModifier of the targeted InvoiceItem instance(s)
	  * @param newTaxModifier A new taxModifier to assign
	  * @return Whether any InvoiceItem instance was affected
	  */
	def taxModifier_=(newTaxModifier: Double)(implicit connection: Connection) = 
		putColumn(model.taxModifierColumn, newTaxModifier)
	
	/**
	  * Updates the unitId of the targeted InvoiceItem instance(s)
	  * @param newUnitId A new unitId to assign
	  * @return Whether any InvoiceItem instance was affected
	  */
	def unitId_=(newUnitId: Int)(implicit connection: Connection) = putColumn(model.unitIdColumn, newUnitId)
}

