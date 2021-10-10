package vf.arbiter.core.database.access.single.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.company.CompanyBankAddressFactory
import vf.arbiter.core.database.model.company.CompanyBankAddressModel
import vf.arbiter.core.model.stored.company.CompanyBankAddress

/**
  * A common trait for access points that return individual and distinct CompanyBankAddresses.
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait UniqueCompanyBankAddressAccess 
	extends SingleRowModelAccess[CompanyBankAddress] 
		with DistinctModelAccess[CompanyBankAddress, Option[CompanyBankAddress], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the company which owns this bank account address. None if no instance (or value) was found.
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
	  * Time when this information was registered. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Whether this is the preferred / primary bank address used by this company. None if no instance (or value) was found.
	  */
	def isDefault(implicit connection: Connection) = pullColumn(model.isDefaultColumn).boolean
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyBankAddressModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyBankAddressFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the address of the targeted CompanyBankAddress instance(s)
	  * @param newAddress A new address to assign
	  * @return Whether any CompanyBankAddress instance was affected
	  */
	def address_=(newAddress: String)(implicit connection: Connection) = 
		putColumn(model.addressColumn, newAddress)
	
	/**
	  * Updates the bankId of the targeted CompanyBankAddress instance(s)
	  * @param newBankId A new bankId to assign
	  * @return Whether any CompanyBankAddress instance was affected
	  */
	def bankId_=(newBankId: Int)(implicit connection: Connection) = putColumn(model.bankIdColumn, newBankId)
	
	/**
	  * Updates the companyId of the targeted CompanyBankAddress instance(s)
	  * @param newCompanyId A new companyId to assign
	  * @return Whether any CompanyBankAddress instance was affected
	  */
	def companyId_=(newCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.companyIdColumn, newCompanyId)
	
	/**
	  * Updates the created of the targeted CompanyBankAddress instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any CompanyBankAddress instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the isDefault of the targeted CompanyBankAddress instance(s)
	  * @param newIsDefault A new isDefault to assign
	  * @return Whether any CompanyBankAddress instance was affected
	  */
	def isDefault_=(newIsDefault: Boolean)(implicit connection: Connection) = 
		putColumn(model.isDefaultColumn, newIsDefault)
}

