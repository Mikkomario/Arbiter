package vf.arbiter.core.database.access.single.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.company.CompanyBankAccountFactory
import vf.arbiter.core.database.model.company.CompanyBankAccountModel
import vf.arbiter.core.model.stored.company.CompanyBankAccount

/**
  * A common trait for access points that return individual and distinct CompanyBankAccounts.
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait UniqueCompanyBankAccountAccess 
	extends SingleRowModelAccess[CompanyBankAccount] 
		with DistinctModelAccess[CompanyBankAccount, Option[CompanyBankAccount], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the company which owns this bank account. None if no instance (or value) was found.
	  */
	def companyId(implicit connection: Connection) = pullColumn(model.companyIdColumn).int
	
	/**
	  * Id of the bank where the company owns an account. None if no instance (or value) was found.
	  */
	def bankId(implicit connection: Connection) = pullColumn(model.bankIdColumn).int
	
	/**
	  * The linked bank account address. None if no instance (or value) was found.
	  */
	def address(implicit connection: Connection) = pullColumn(model.addressColumn).string
	
	/**
	  * Id of the user linked with this CompanyBankAccount. None if no instance (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this information was registered. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Time when this CompanyBankAccount became deprecated. None while this CompanyBankAccount is still valid.. None if no instance (or value) was found.
	  */
	def deprecatedAfter(implicit connection: Connection) = pullColumn(model.deprecatedAfterColumn).instant
	
	/**
	  * Whether this bank account information was written by the company authorities. None if no instance (or value) was found.
	  */
	def isOfficial(implicit connection: Connection) = pullColumn(model.isOfficialColumn).boolean
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyBankAccountModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyBankAccountFactory
	
	
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

