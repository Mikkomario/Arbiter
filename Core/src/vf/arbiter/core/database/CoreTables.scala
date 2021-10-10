package vf.arbiter.core.database

import utopia.citadel.database.Tables
import utopia.vault.model.immutable.Table

/**
  * Used for accessing the database tables introduced in this project
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object CoreTables
{
	// COMPUTED	--------------------
	
	/**
	  * Table that contains Banks (Represents a bank (used with bank addresses))
	  */
	def bank = apply("bank")
	
	/**
	  * Table that contains Companies (Represents a registered company (or an individual person))
	  */
	def company = apply("company")
	
	/**
	  * Table that contains CompanyBankAddresses (Used for listing which bank addresses belong to which company)
	  */
	def companyBankAddress = apply("company_bank_address")
	
	/**
	  * Table that contains Counties (Represents a county within a country)
	  */
	def county = apply("county")
	
	/**
	  * Table that contains Invoices (Represents a bill / an invoice sent by one company to another to request a monetary transfer / payment)
	  */
	def invoice = apply("invoice")
	
	/**
	  * Table that contains InvoiceItems (Represents an individual sold item or service)
	  */
	def invoiceItem = apply("invoice_item")
	
	/**
	  * Table that contains ItemUnits (Represents a unit in which items can be counted)
	  */
	def itemUnit = apply("item_unit")
	
	/**
	  * Table that contains ItemUnitDescriptions (Links ItemUnits with their descriptions)
	  */
	def itemUnitDescription = apply("item_unit_description")
	
	/**
	  * Table that contains OrganizationCompanies (Connects organizations with their owned companies)
	  */
	def organizationCompany = apply("organization_company")
	
	/**
	  * Table that contains PostalCodes (Represents a postal code, which represents an area within a county)
	  */
	def postalCode = apply("postal_code")
	
	/**
	  * Table that contains StreetAddresses (Represents a specific street address)
	  */
	def streetAddress = apply("street_address")
	
	
	// OTHER	--------------------
	
	private def apply(tableName: String): Table = Tables(tableName)
}

