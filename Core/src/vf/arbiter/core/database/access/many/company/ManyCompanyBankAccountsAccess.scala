package vf.arbiter.core.database.access.many.company

import java.time.Instant
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.company.CompanyBankAccountFactory
import vf.arbiter.core.database.model.company.CompanyBankAccountModel
import vf.arbiter.core.model.stored.company.CompanyBankAccount

object ManyCompanyBankAccountsAccess
{
	// NESTED	--------------------
	
	private class ManyCompanyBankAccountsSubView(override val parent: ManyRowModelAccess[CompanyBankAccount], 
		override val filterCondition: Condition) 
		extends ManyCompanyBankAccountsAccess with SubView
}

/**
  * A common trait for access points which target multiple CompanyBankAccounts at a time
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
trait ManyCompanyBankAccountsAccess extends ManyRowModelAccess[CompanyBankAccount] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * companyIds of the accessible CompanyBankAccounts
	  */
	def companyIds(implicit connection: Connection) = 
		pullColumn(model.companyIdColumn).flatMap { value => value.int }
	
	/**
	  * bankIds of the accessible CompanyBankAccounts
	  */
	def bankIds(implicit connection: Connection) = pullColumn(model.bankIdColumn)
		.flatMap { value => value.int }
	
	/**
	  * addresses of the accessible CompanyBankAccounts
	  */
	def addresses(implicit connection: Connection) = 
		pullColumn(model.addressColumn).flatMap { value => value.string }
	
	/**
	  * creatorIds of the accessible CompanyBankAccounts
	  */
	def creatorIds(implicit connection: Connection) = 
		pullColumn(model.creatorIdColumn).flatMap { value => value.int }
	
	/**
	  * createds of the accessible CompanyBankAccounts
	  */
	def createds(implicit connection: Connection) = 
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	
	/**
	  * deprecatedAfters of the accessible CompanyBankAccounts
	  */
	def deprecatedAfters(implicit connection: Connection) = 
		pullColumn(model.deprecatedAfterColumn).flatMap { value => value.instant }
	
	/**
	  * areOfficial of the accessible CompanyBankAccounts
	  */
	def areOfficial(implicit connection: Connection) = 
		pullColumn(model.isOfficialColumn).flatMap { value => value.boolean }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyBankAccountModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyBankAccountFactory
	
	override protected def defaultOrdering = None
	
	override def filter(additionalCondition: Condition): ManyCompanyBankAccountsAccess = 
		new ManyCompanyBankAccountsAccess.ManyCompanyBankAccountsSubView(this, additionalCondition)
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the address of the targeted CompanyBankAccount instance(s)
	  * @param newAddress A new address to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def address_=(newAddress: String)(implicit connection: Connection) = 
		putColumn(model.addressColumn, newAddress)
	
	/**
	  * Updates the bankId of the targeted CompanyBankAccount instance(s)
	  * @param newBankId A new bankId to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def bankId_=(newBankId: Int)(implicit connection: Connection) = putColumn(model.bankIdColumn, newBankId)
	
	/**
	  * Updates the companyId of the targeted CompanyBankAccount instance(s)
	  * @param newCompanyId A new companyId to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def companyId_=(newCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.companyIdColumn, newCompanyId)
	
	/**
	  * Updates the created of the targeted CompanyBankAccount instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted CompanyBankAccount instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the deprecatedAfter of the targeted CompanyBankAccount instance(s)
	  * @param newDeprecatedAfter A new deprecatedAfter to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def deprecatedAfter_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.deprecatedAfterColumn, newDeprecatedAfter)
	
	/**
	  * Updates the isOfficial of the targeted CompanyBankAccount instance(s)
	  * @param newIsOfficial A new isOfficial to assign
	  * @return Whether any CompanyBankAccount instance was affected
	  */
	def isOfficial_=(newIsOfficial: Boolean)(implicit connection: Connection) = 
		putColumn(model.isOfficialColumn, newIsOfficial)
}

