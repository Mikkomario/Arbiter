package vf.arbiter.command.controller

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Pair
import utopia.flow.collection.immutable.range.Span
import utopia.flow.operator.numeric.DoubleLike
import utopia.flow.operator.sign.{Sign, SignOrZero}
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.time.TimeExtensions._
import utopia.flow.time.Today
import utopia.metropolis.model.cached.LanguageIds
import utopia.vault.database.Connection
import vf.arbiter.core.database.access.many.company.{DbCompanies, DbCompanyProducts, DbManyCompanyDetails}
import vf.arbiter.core.database.access.many.invoice.{DbInvoiceItems, DbInvoices}
import vf.arbiter.core.model.combined.company.DescribedCompanyProduct
import vf.arbiter.core.model.combined.invoice.InvoiceWithItems

import java.nio.file.Path
import java.time.{Month, Year}
import scala.collection.MapView
import scala.io.Codec
import scala.util.Success

/**
 * Used for exporting summary data for spreadsheets
 * @author Mikko Hilpinen
 * @since 20.11.2021, v1.2
 */
object ExportSummary
{
	private implicit val codec: Codec = Codec.UTF8
	
	/**
	 * Exports summary data in csv format
	 * @param companyId Id of the company whose data is exported
	 * @param directory Directory where the data will be exported to
	 * @param year Year from which data is collected (default = current)
	 * @param months The months from which the data is collected
	 *               (default = all months up till and including the current month)
	 * @param connection Implicit DB Connection
	 * @param languageIds Language ids to use
	 * @return Success or failure
	 */
	def asCsv(companyId: Int, directory: Path = "summaries", year: Year = Today.year,
	          months: Span[Month] = Span(Month.JANUARY, Today.month))
	         (implicit connection: Connection, languageIds: LanguageIds) =
	{
		directory.createDirectories().flatMap { directory =>
			// Collects all invoice data concerning this company (for the targeted year)
			val invoices = DbInvoices.during(months.mapTo { year/_ }).sentByCompanyWithId(companyId)
			// Reads associated data (items, products, recipient companies)
			val invoiceIds = invoices.map { _.id }.toSet
			val items = DbInvoiceItems.forAnyOfInvoices(invoiceIds).pull
			val products = DbCompanyProducts(items.map { _.productId }.toSet).described
			val customerDetails = DbManyCompanyDetails(invoices.map { _.recipientCompanyDetailsId }.toSet).pull
			val customers = DbCompanies(customerDetails.map { _.companyId }.toSet).detailed.pull
			//println(s"Found ${invoices.size} invoices with ${items.size} items")
			
			// Formats the data so that it can be accessed more easily
			val itemsPerInvoiceId = items.groupBy { _.invoiceId }
			val invoicesWithItems = invoices.map { i => i.withItems(itemsPerInvoiceId.getOrElse(i.id, Vector())) }
			val yCodePerCompanyId = customers.map { c => c.id -> c.yCode }.toMap
			val namePerCompanyId = customers.map { c => c.id -> c.details.name }.toMap
			val companyIdPerDetailsId = customerDetails.map { d => d.id -> d.companyId }.toMap
			val yCodePerDetailsId = companyIdPerDetailsId.view.mapValues { companyId => yCodePerCompanyId(companyId) }
			val namePerDetailsId = companyIdPerDetailsId.view.mapValues { companyId => namePerCompanyId(companyId) }
			val productPerId = products.map { p => p.id -> p }.toMap
			
			// Writes multiple csv documents based on the collected data
			exportMonthlyInvoices(directory/"invoices", invoicesWithItems, yCodePerDetailsId, namePerDetailsId,
				productPerId)
				.flatMap { _ => exportTotals(directory, invoicesWithItems, productPerId) }
				.flatMap { _ => exportGrouped(directory/"sales-by-customer.csv",
					invoicesWithItems.groupBy { i => namePerDetailsId(i.recipientCompanyDetailsId) }) }
				.flatMap { _ =>
					exportGroupedTotals(directory/"sales-by-product.csv",
						invoicesWithItems.flatMap { i =>
							val month = i.created.toLocalDate.month
							i.items.groupMap { i => productPerId(i.productId) } { _.price }
								.map { case (product, prices) => (product, month, prices.sum) }
						}.groupMap { _._1 } { case (_, m, p) => m -> p }
							.map { case (product, prices) => product.name ->
								prices.groupMapReduce { _._1 } { _._2 } { _ + _ } })
				}
		}
	}
	
	private def exportMonthlyInvoices(directory: Path, invoices: Vector[InvoiceWithItems],
	                                  companyYCodeForDetailsId: MapView[Int, String],
	                                  companyNameForDetailsId: MapView[Int, String],
	                                  productPerId: Map[Int, DescribedCompanyProduct]) =
	{
		// Writes a document for each month's invoices
		invoices.iterator.groupBy { _.created.toLocalDateTime.toLocalDate.yearMonth }.toVector
			.tryForeach { case (month, invoices) =>
				(directory/s"${month.year}-${month.getMonthValue}-invoices.csv").createParentDirectories()
					.flatMap { targetPath =>
						val headerLine = "Invoice Id;Reference Code;Customer Y-Code;Customer Name;Amount €;Tax €;Total €;Description"
						val contentLines = invoices.map { invoice =>
							val total = Total.from(invoice, productPerId)
							s"${invoice.id};${invoice.referenceCode};${
								companyYCodeForDetailsId(invoice.recipientCompanyDetailsId)};${
								companyNameForDetailsId(invoice.recipientCompanyDetailsId)};${total.price};${total.tax};${
								total.priceWithTax};${invoice.items.map { _.description }.mkString(", ")}"
						}
						targetPath.writeLines(headerLine +: contentLines)
					}
			}
	}
	
	private def exportTotals(directory: Path, invoices: Vector[InvoiceWithItems],
	                         productPerId: Map[Int, DescribedCompanyProduct]) =
	{
		// Won't write anything if there are no invoices
		if (invoices.exists { _.items.exists { _.price > 0 } }) {
			directory.createDirectories().flatMap { directory =>
				// Collects total price, tax and price + tax for each month
				val monthTotals = invoices.groupMapReduce { _.created.toLocalDate.month } {
					i => Total.from(i, productPerId) } { _ + _ }
				val monthsWithTotals = Month.values().iterator
					.map { m => m -> monthTotals.getOrElse(m, Total.zero) }
					.dropWhile { _._2.isZero }.toVector.dropRightWhile { _._2.isZero }
				
				val yearTotal = monthTotals.valuesIterator.reduce { _ + _ }
				val averagePrice = yearTotal.price / monthsWithTotals.size
				
				// Writes the month totals -document
				val zeroMonth = monthsWithTotals.head._1.previous -> Total.zero
				(directory/"totals-monthly.csv").writeLines(
					"Index;Month;Invoices;Without Tax;Tax;Total;Change %;Change % to Average" +:
						(zeroMonth +: monthsWithTotals).paired.map { case Pair((_, previous), (month, total)) =>
							val change = total - previous
							s"${month.getValue};$month;${total.invoiceCount};${doubleStr(total.price)};${
								doubleStr(total.tax)};${doubleStr(total.priceWithTax)};${
								percentageStr(change.price, previous.price)};${
								percentageStr(total.price - averagePrice, averagePrice)}"
						})
			}
		}
		else
			Success(())
	}
	
	private def exportGrouped[A](path: Path, invoices: Map[A, Vector[InvoiceWithItems]]) = {
		// Calculates the total sales for each target per each month
		exportGroupedTotals(path,
			invoices.view.mapValues { _.groupMapReduce { _.created.toLocalDate.month } { _.price } { _ + _ } }.toMap)
	}
	
	private def exportGroupedTotals[A](path: Path, sales: Map[A, Map[Month, Double]]) = {
		// Calculates the total sales for each target per each month
		val groupTotals = sales.toVector.sortBy { _._2.valuesIterator.sum }
		// Writes months as rows and targets as columns
		path.createParentDirectories().flatMap { _.writeLines(groupTotals.map { _._1.toString }.mkString(";") +:
			Month.values().iterator.map { month =>
				groupTotals.map { case (_, totals) => doubleStr(totals.getOrElse(month, 0.0)) }.mkString(";") }) }
	}
	
	private def percentageStr(a: Double, total: Double) = {
		if (total == 0)
			""
		else {
			val percentage = (a/total * 100).round
			val percentageStr = doubleStr(percentage.toDouble) + "%"
			if (percentage >= 0)
				s"+$percentageStr"
			else
				percentageStr
		}
	}
	
	private def doubleStr(a: Double) = (Math.round(a * 100) / 100).toString
	
	
	// NESTED   ---------------------------------
	
	object Total
	{
		val zero = apply(0, 0, 0)
		
		def from(invoice: InvoiceWithItems, productPerId: Map[Int, DescribedCompanyProduct]) =
			apply(1, invoice.items.map { _.price }.sum,
				invoice.items.map { i => i.price * productPerId(i.productId).data.taxModifier }.sum)
	}
	
	case class Total(invoiceCount: Int, price: Double, tax: Double) extends DoubleLike[Total]
	{
		// ATTRIBUTES   ----------------------
		
		override lazy val sign: SignOrZero = Sign.of(price)
		
		
		// COMPUTED --------------------------
		
		def priceWithTax = price + tax
		
		
		// IMPLEMENTED  ----------------------
		
		override def self: Total = this
		override def zero = Total.zero
		
		override def length = price
		
		override def compareTo(o: Total) = price.compareTo(o.price)
		
		def +(other: Total) = Total(invoiceCount + other.invoiceCount, price + other.price, tax + other.tax)
		override def *(mod: Double) = Total(invoiceCount, price * mod, tax * mod)
		
	}
}
