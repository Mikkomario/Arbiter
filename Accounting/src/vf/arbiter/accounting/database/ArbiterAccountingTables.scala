package vf.arbiter.accounting.database

import utopia.citadel.database.Tables
import utopia.citadel.model.cached.DescriptionLinkTable
import utopia.vault.model.immutable.Table

/**
  * Used for accessing the database tables introduced in this project
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object ArbiterAccountingTables
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Table that contains transaction descriptions (Links Transactions with their descriptions)
	  */
	lazy val transactionDescription = DescriptionLinkTable(apply("transaction_description"), "transactionId")
	
	/**
	  * Table that contains transaction type descriptions (Links TransactionTypes with their descriptions)
	  */
	lazy val transactionTypeDescription = DescriptionLinkTable(apply("transaction_type_description"), "typeId")
	
	
	// COMPUTED	--------------------
	
	/**
	  * Table that contains account balances (Represents a balance (monetary amount) on a bank account at a
	  *  specific time)
	  */
	def accountBalance = apply("account_balance")
	
	/**
	  * Table that contains allocation targets (Represents a goal or a target for money-allocation)
	  */
	def allocationTarget = apply("allocation_target")
	
	/**
	  * Table that contains invoice payments (Links an invoice with the transaction where it was paid)
	  */
	def invoicePayment = apply("invoice_payment")
	
	/**
	  * Table that contains party entries (Represents a transaction party as described on a bank statement)
	  */
	def partyEntry = apply("party_entry")
	
	/**
	  * Table that contains transactions (Represents a transaction from or to a bank account. Only includes data from
	  *  a bank statement.)
	  */
	def transaction = apply("transaction")
	
	/**
	  * Table that contains transaction evaluations (Lists information that's added on top of a raw
	  *  transaction record. Typically based on user input.)
	  */
	def transactionEvaluation = apply("transaction_evaluation")
	
	/**
	  * Table that contains transaction types (Represents a category or a type of transaction)
	  */
	def transactionType = apply("transaction_type")
	
	/**
	  * Table that contains type specific allocation targets (Represents an allocation / 
	  * target for a specific transaction-type)
	  */
	def typeSpecificAllocationTarget = apply("type_specific_allocation_target")
	
	
	// OTHER	--------------------
	
	private def apply(tableName: String): Table = Tables(tableName)
}

