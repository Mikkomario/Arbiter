package vf.arbiter.core.database.access.many.company

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.{ManyModelAccess, ManyRowModelAccess}
import utopia.vault.nosql.template.Indexed
import utopia.vault.sql.Condition
import utopia.vault.sql.SqlExtensions._
import vf.arbiter.core.database.model.company.CompanyBankAccountModel

import java.time.Instant

/**
  * A common trait for access points which target multiple company bank accounts or related items at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
trait ManyCompanyBankAccountsAccessLike[+A, +Repr <: ManyModelAccess[A]] extends ManyRowModelAccess[A] with Indexed
{
	// ABSTRACT --------------------
	
	protected def _filter(condition: Condition): Repr
	
	
	// COMPUTED	--------------------
	
	/**
	  * companyIds of the accessible CompanyBankAccounts
	  */
	def companyIds(implicit connection: Connection) = 
		pullColumn(accountModel.companyIdColumn).flatMap { value => value.int }
	/**
	  * bankIds of the accessible CompanyBankAccounts
	  */
	def bankIds(implicit connection: Connection) = pullColumn(accountModel.bankIdColumn)
		.flatMap { value => value.int }
	/**
	  * addresses of the accessible CompanyBankAccounts
	  */
	def addresses(implicit connection: Connection) =
		pullColumn(accountModel.addressColumn).flatMap { value => value.string }
	/**
	  * creatorIds of the accessible CompanyBankAccounts
	  */
	def creatorIds(implicit connection: Connection) =
		pullColumn(accountModel.creatorIdColumn).flatMap { value => value.int }
	/**
	  * createds of the accessible CompanyBankAccounts
	  */
	def createds(implicit connection: Connection) =
		pullColumn(accountModel.createdColumn).flatMap { value => value.instant }
	/**
	  * deprecatedAfters of the accessible CompanyBankAccounts
	  */
	def deprecatedAfters(implicit connection: Connection) =
		pullColumn(accountModel.deprecatedAfterColumn).flatMap { value => value.instant }
	/**
	  * areOfficial of the accessible CompanyBankAccounts
	  */
	def areOfficial(implicit connection: Connection) =
		pullColumn(accountModel.isOfficialColumn).flatMap { value => value.boolean }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def accountModel = CompanyBankAccountModel
	
	
	// IMPLEMENTED  ----------------
	
	override def filter(additionalCondition: Condition) = _filter(additionalCondition)
	
	
	// OTHER	--------------------
	
	/**
	 * @param companyId Id of the targeted company
	 * @return Accounts belonging to that company
	 */
	def belongingToCompanyWithId(companyId: Int) = filter(accountModel.withCompanyId(companyId).toCondition)
	/**
	 * @param companyIds Ids of targeted companies
	 * @return An access point to bank accounts belonging to those companies
	 */
	def belongingToAnyOfCompanies(companyIds: Iterable[Int]) = filter(accountModel.companyIdColumn in companyIds)
	/**
	 * @param bankIds Ids of the targeted banks
	 * @return An access point to accounts in those banks
	 */
	def inAnyOfBanks(bankIds: Iterable[Int]) = filter(accountModel.bankIdColumn in bankIds)
	/**
	 * @param accountAddresses A collection of bank account addresses (IBANs)
	 * @return An access point to those addresses (in any bank)
	 */
	def withAnyOfAddresses(accountAddresses: Iterable[String]) =
		filter(accountModel.addressColumn in accountAddresses)
	
	/**
	  * Updates the address of the targeted CompanyBankAccount instance(s)
	  * @param newAddress A new address to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def address_=(newAddress: String)(implicit connection: Connection) =
		putColumn(accountModel.addressColumn, newAddress)
	/**
	  * Updates the bankId of the targeted CompanyBankAccount instance(s)
	  * @param newBankId A new bankId to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def bankId_=(newBankId: Int)(implicit connection: Connection) = putColumn(accountModel.bankIdColumn, newBankId)
	/**
	  * Updates the companyId of the targeted CompanyBankAccount instance(s)
	  * @param newCompanyId A new companyId to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def companyId_=(newCompanyId: Int)(implicit connection: Connection) =
		putColumn(accountModel.companyIdColumn, newCompanyId)
	/**
	  * Updates the created of the targeted CompanyBankAccount instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) =
		putColumn(accountModel.createdColumn, newCreated)
	/**
	  * Updates the creatorId of the targeted CompanyBankAccount instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) =
		putColumn(accountModel.creatorIdColumn, newCreatorId)
	/**
	  * Updates the deprecatedAfter of the targeted CompanyBankAccount instance(s)
	  * @param newDeprecatedAfter A new deprecatedAfter to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def deprecatedAfter_=(newDeprecatedAfter: Instant)(implicit connection: Connection) =
		putColumn(accountModel.deprecatedAfterColumn, newDeprecatedAfter)
	/**
	  * Updates the isOfficial of the targeted CompanyBankAccount instance(s)
	  * @param newIsOfficial A new isOfficial to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def isOfficial_=(newIsOfficial: Boolean)(implicit connection: Connection) =
		putColumn(accountModel.isOfficialColumn, newIsOfficial)
}

