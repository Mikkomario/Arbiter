package vf.arbiter.command.app

import utopia.flow.util.console.ConsoleExtensions._
import utopia.vault.database.Connection
import vf.arbiter.core.database.access.many.company.DbBanks
import vf.arbiter.core.database.access.single.company.DbCompany
import vf.arbiter.core.database.model.company.{BankModel, CompanyBankAccountModel}
import vf.arbiter.core.model.combined.company.FullCompanyBankAccount
import vf.arbiter.core.model.partial.company.{BankData, CompanyBankAccountData}

import scala.io.StdIn

/**
 * Contains interactive functions for dealing with bank accounts
 * @author Mikko Hilpinen
 * @since 5.5.2022, v1.3
 */
object BankActions
{
	/**
	 * Asks the user to select a bank account. Also allows account creation.
	 * @param userId Id of the user who's selecting the account
	 * @param companyId Id of the company for which the account is selected
	 * @param connection Implicit DB connection
	 * @return Selected or created account. None if no account was selected or created.
	 */
	def selectOrCreateBankAccount(userId: Int, companyId: Int)(implicit connection: Connection) =
	{
		val existingAccounts = DbCompany(companyId).bankAccounts.full.pull
		if (existingAccounts.isEmpty)
			createBankAccount(userId, companyId)
		else
			ActionUtils.selectOrInsert(existingAccounts.map { a => a -> s"${a.bank.name}: ${a.address}" }) {
				createBankAccount(userId, companyId)
			}
	}
	
	/**
	 * Creates a new bank account for the specified company
	 * @param userId Id of the user adding the bank account
	 * @param companyId Id of the company for which the bank account is added
	 * @param connection Implicit DB Connection
	 * @return Inserted bank account. None if user cancelled.
	 */
	def createBankAccount(userId: Int, companyId: Int)(implicit connection: Connection) =
		selectOrCreateBank(userId).flatMap { bank =>
			println(s"What's your bank account number (IBAN) in ${bank.name}?")
			StdIn.readNonEmptyLine(
				retryPrompt = "This information is required. Leaving empty will cancel this process.").map { address =>
				// TODO: Should check for duplicates
				val newAccount = CompanyBankAccountModel.insert(
					CompanyBankAccountData(companyId, bank.id, address, Some(userId)))
				FullCompanyBankAccount(newAccount, bank)
			}
		}
	
	/**
	 * Allows the user to select from existing banks or to create a new one
	 * @param userId Id of the user
	 * @param connection Implicit DB Connection
	 * @return Selected or created bank. None if user didn't want to select nor create.
	 */
	def selectOrCreateBank(userId: Int)(implicit connection: Connection) =
	{
		val existingBanks = DbBanks.pull
		if (existingBanks.isEmpty)
			createBank(userId)
		else
			ActionUtils.selectOrInsert(existingBanks.map { b => b -> b.nameAndBic }, "bank") { createBank(userId) }
	}
	
	/**
	 * Creates a new bank, provided the user gives the necessary information
	 * @param userId Id of the user who's adding information
	 * @param connection Implicit DB Connection
	 * @return Created bank. None if user didn't provide information.
	 */
	// TODO: Should probably check for duplicates
	def createBank(userId: Int)(implicit connection: Connection) =
	{
		val retryPrompt = "This information is required for bank creation. Leaving empty will cancel this process."
		StdIn.readNonEmptyLine("What's the name of the bank you're using?", retryPrompt).flatMap { name =>
			StdIn.readNonEmptyLine("What's the BIC of the bank you're using?", retryPrompt).map { bic =>
				BankModel.insert(BankData(name, bic, Some(userId)))
			}
		}
	}
}
