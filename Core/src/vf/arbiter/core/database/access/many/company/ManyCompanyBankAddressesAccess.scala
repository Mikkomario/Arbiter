package vf.arbiter.core.database.access.many.company

import java.time.Instant
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.company.CompanyBankAddressFactory
import vf.arbiter.core.database.model.company.CompanyBankAddressModel
import vf.arbiter.core.model.stored.company.CompanyBankAddress

/**
  * A common trait for access points which target multiple CompanyBankAddresses at a time
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait ManyCompanyBankAddressesAccess extends ManyRowModelAccess[CompanyBankAddress] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * companyIds of the accessible CompanyBankAddresses
	  */
	def companyIds(implicit connection: Connection) = 
		pullColumn(model.companyIdColumn).flatMap { value => value.int }
	
	/**
	  * bankIds of the accessible CompanyBankAddresses
	  */
	def bankIds(implicit connection: Connection) = pullColumn(model.bankIdColumn)
		.flatMap { value => value.int }
	
	/**
	  * addresses of the accessible CompanyBankAddresses
	  */
	def addresses(implicit connection: Connection) = 
		pullColumn(model.addressColumn).flatMap { value => value.string }
	
	/**
	  * createds of the accessible CompanyBankAddresses
	  */
	def createds(implicit connection: Connection) = 
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	
	/**
	  * defaults of the accessible CompanyBankAddresses
	  */
	def defaults(implicit connection: Connection) = 
		pullColumn(model.isDefaultColumn).flatMap { value => value.boolean }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyBankAddressModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyBankAddressFactory
	
	override protected def defaultOrdering = Some(factory.defaultOrdering)
	
	
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

