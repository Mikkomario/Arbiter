package vf.arbiter.command.app

import utopia.flow.time.Days
import utopia.flow.util.console.ConsoleExtensions._
import utopia.vault.database.Connection
import vf.arbiter.core.model.stored.company.Company

import scala.io.StdIn

/**
 * Contains interactive invoice-related actions
 * @author Mikko Hilpinen
 * @since 11.10.2021, v0.1
 */
object InvoiceActions
{
	/*
	When creating an invoice, we need following information:
		- Origin company
		- Target company
		- Duration days
		- Item delivery date (opt)
		- Product lines
			- Description
			- Amount & Unit
			- Price per unit
			- Tax %
	 */
	def create(userId: Int, company: Company)(implicit connection: Connection) =
	{
		// Selects the target company
		println("Which company this invoice is for?")
		println("Hint: If searching for an existing company, write a part of that company's name")
		StdIn.readNonEmptyLine().flatMap { CompanyActions.findOrCreateOne(_) }
			.map { targetCompany =>
				// Asks for basic information
				val duration = Days(StdIn.read(
					"How many days does the company have time to pay this bill? (default = 30)")
					.intOr(30))
				println("When were the services or items delivered for the customer?")
				println("Allowed formats: YYYY-MM-DD and DD.MM.YYYY")
				println("Leave empty if not applicable")
				val deliveryDate = StdIn.read().localDate
				
				// Asks for product information
				Iterator.iterate(1) { _ + 1 }.map { index =>
					println(s"Please add the description of the product line #$index (leave empty to quit adding product lines)")
					StdIn.readNonEmptyLine().map { description =>
						// TODO: Select unit, then amount (requires unit listing)
					}
				}
			}
	}
}
