package vf.arbiter.core.database.access.single.invoice

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.invoice.InvoiceItemFactory
import vf.arbiter.core.database.model.invoice.InvoiceItemModel
import vf.arbiter.core.model.stored.invoice.InvoiceItem

/**
  * A common trait for access points that return individual and distinct InvoiceItems.
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait UniqueInvoiceItemAccess 
	extends SingleRowModelAccess[InvoiceItem] 
		with DistinctModelAccess[InvoiceItem, Option[InvoiceItem], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the invoice on which this item appears. None if no instance (or value) was found.
	  */
	def invoiceId(implicit connection: Connection) = pullColumn(model.invoiceIdColumn).int
	
	/**
	  * Name or description of this item. None if no instance (or value) was found.
	  */
	def description(implicit connection: Connection) = pullColumn(model.descriptionColumn).string
	
	/**
	  * Amount of items sold within the specified unit. None if no instance (or value) was found.
	  */
	def amount(implicit connection: Connection) = pullColumn(model.amountColumn).double
	
	/**
	  * Unit in which these items are sold. None if no instance (or value) was found.
	  */
	def unitId(implicit connection: Connection) = pullColumn(model.unitIdColumn).int
	
	/**
	  * Euro (€) price per each sold unit of this item, 
		without taxes applied. None if no instance (or value) was found.
	  */
	def pricePerUnit(implicit connection: Connection) = pullColumn(model.pricePerUnitColumn).double
	
	/**
	  * A modifier that is applied to this item's price to get the applied tax. None if no instance (or value) was found.
	  */
	def taxModifier(implicit connection: Connection) = pullColumn(model.taxModifierColumn).double
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoiceItemModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceItemFactory
	
	
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

