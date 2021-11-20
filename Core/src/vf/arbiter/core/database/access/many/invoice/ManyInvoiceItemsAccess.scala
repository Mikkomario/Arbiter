package vf.arbiter.core.database.access.many.invoice

import utopia.flow.generic.ValueConversions._
import utopia.metropolis.model.cached.LanguageIds
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import utopia.vault.sql.SqlExtensions._
import vf.arbiter.core.database.access.many.company.DbCompanyProducts
import vf.arbiter.core.database.factory.invoice.InvoiceItemFactory
import vf.arbiter.core.database.model.invoice.InvoiceItemModel
import vf.arbiter.core.model.combined.company.FullCompanyProduct
import vf.arbiter.core.model.stored.invoice.InvoiceItem

object ManyInvoiceItemsAccess
{
	// NESTED	--------------------
	
	private class ManyInvoiceItemsSubView(override val parent: ManyRowModelAccess[InvoiceItem],
	                                      override val filterCondition: Condition)
		extends ManyInvoiceItemsAccess with SubView
}

/**
 * A common trait for access points which target multiple InvoiceItems at a time
 * @author Mikko Hilpinen
 * @since 2021-10-14
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
	 * productIds of the accessible InvoiceItems
	 */
	def productIds(implicit connection: Connection) =
		pullColumn(model.productIdColumn).flatMap { value => value.int }
	/**
	 * descriptions of the accessible InvoiceItems
	 */
	def descriptions(implicit connection: Connection) =
		pullColumn(model.descriptionColumn).flatMap { value => value.string }
	/**
	 * perUnitPrices of the accessible InvoiceItems
	 */
	def perUnitPrices(implicit connection: Connection) =
		pullColumn(model.pricePerUnitColumn).flatMap { value => value.double }
	/**
	 * unitsSold of the accessible InvoiceItems
	 */
	def unitsSold(implicit connection: Connection) =
		pullColumn(model.unitsSoldColumn).flatMap { value => value.double }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	 * Factory used for constructing database the interaction models
	 */
	protected def model = InvoiceItemModel
	
	/**
	 * Reads these invoice items and attaches all linked information
	 * @param connection Implicit DB Connection
	 * @param languageIds Ids of the languages in which descriptions are read
	 * @return Full copies of these invoice items
	 */
	def full(implicit connection: Connection, languageIds: LanguageIds) =
	{
		// Reads invoice items
		val items = pull
		// Reads associated product information
		val productIds = items.map { _.productId }.toSet
		val products = if (productIds.isEmpty) Vector() else DbCompanyProducts(productIds).described
		// Reads associated unit information
		val unitIds = products.map { _.wrapped.unitId }.toSet
		val units = if (unitIds.isEmpty) Vector() else DbItemUnits(unitIds).described
		val unitsById = units.map { u => u.id -> u }.toMap
		val productsById: Map[Int, FullCompanyProduct] =
			products.map { p => p.id -> (p + unitsById(p.wrapped.unitId)) }.toMap
		// Combines the gathered information
		items.map { item => item + productsById(item.productId) }
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceItemFactory
	
	override protected def defaultOrdering = None
	
	override def filter(additionalCondition: Condition): ManyInvoiceItemsAccess =
		new ManyInvoiceItemsAccess.ManyInvoiceItemsSubView(this, additionalCondition)
	
	
	// OTHER	--------------------
	
	/**
	 * @param invoiceId Id of the targeted invoice
	 * @return An access point to items belonging to that invoice
	 */
	def forInvoiceWithId(invoiceId: Int) = filter(model.withInvoiceId(invoiceId).toCondition)
	/**
	 * @param invoiceIds Ids of targeted invoices
	 * @return An access point to all of those invoices items
	 */
	def forAnyOfInvoices(invoiceIds: Iterable[Int]) =
		filter(model.invoiceIdColumn in invoiceIds)
	
	/**
	 * Reads these invoice items and attaches all linked information
	 * @param languageId Id of the language in which these items are read
	 * @param connection Implicit DB Connection
	 * @return Full copies of these invoice items
	 */
	def fullInLanguageWithId(languageId: Int)(implicit connection: Connection) =
	{
		implicit val languageIds: LanguageIds = LanguageIds(languageId)
		full
	}
	
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
	 * Updates the productId of the targeted InvoiceItem instance(s)
	 * @param newProductId A new productId to assign
	 * @return Whether any InvoiceItem instance was affected
	 */
	def productId_=(newProductId: Int)(implicit connection: Connection) =
		putColumn(model.productIdColumn, newProductId)
	/**
	 * Updates the unitsSold of the targeted InvoiceItem instance(s)
	 * @param newUnitsSold A new unitsSold to assign
	 * @return Whether any InvoiceItem instance was affected
	 */
	def unitsSold_=(newUnitsSold: Double)(implicit connection: Connection) =
		putColumn(model.unitsSoldColumn, newUnitsSold)
}

