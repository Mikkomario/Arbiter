package vf.arbiter.core.model.combined.invoice

import utopia.flow.view.template.Extender
import vf.arbiter.core.model.combined.company.{FullCompanyBankAccount, FullyDetailedCompany}
import vf.arbiter.core.model.partial.invoice.InvoiceData
import vf.arbiter.core.model.stored.invoice.Invoice

/**
 * Contains all information within an invoice and its nested items
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
case class FullInvoice(invoice: Invoice, senderCompany: FullyDetailedCompany, recipientCompany: FullyDetailedCompany,
                       senderBankAccount: FullCompanyBankAccount, items: Vector[FullInvoiceItem])
	extends Extender[InvoiceData]
{
	// COMPUTED ------------------------------------
	
	/**
	 * @return Id of this invoice
	 */
	def id = invoice.id
	
	/**
	 * @return Total price of this invoice, without applying any taxes
	 */
	def price = items.map { _.price }.sum
	/**
	 * @return Total amount of tax to pay on top of this invoice's price
	 */
	def tax = items.map { _.tax }.sum
	/**
	 * @return Total price of this invoice, including taxes
	 */
	def totalPrice = price + tax
	
	/**
	 * @return Copy of this invoice without all data included
	 */
	def toInvoiceWithItems = InvoiceWithItems(invoice, items.map { _.item })
	
	
	// IMPLEMENTED  --------------------------------
	
	override def wrapped = invoice.data
}