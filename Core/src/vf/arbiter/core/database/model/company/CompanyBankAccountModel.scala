package vf.arbiter.core.database.model.company

import java.time.Instant
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import utopia.vault.nosql.storable.deprecation.DeprecatableAfter
import vf.arbiter.core.database.factory.company.CompanyBankAccountFactory
import vf.arbiter.core.model.partial.company.CompanyBankAccountData
import vf.arbiter.core.model.stored.company.CompanyBankAccount

/**
  * Used for constructing CompanyBankAccountModel instances and for inserting CompanyBankAccounts to the database
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object CompanyBankAccountModel 
	extends DataInserter[CompanyBankAccountModel, CompanyBankAccount, CompanyBankAccountData] 
		with DeprecatableAfter[CompanyBankAccountModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains CompanyBankAccount companyId
	  */
	val companyIdAttName = "companyId"
	
	/**
	  * Name of the property that contains CompanyBankAccount bankId
	  */
	val bankIdAttName = "bankId"
	
	/**
	  * Name of the property that contains CompanyBankAccount address
	  */
	val addressAttName = "address"
	
	/**
	  * Name of the property that contains CompanyBankAccount creatorId
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains CompanyBankAccount created
	  */
	val createdAttName = "created"
	
	/**
	  * Name of the property that contains CompanyBankAccount deprecatedAfter
	  */
	val deprecatedAfterAttName = "deprecatedAfter"
	
	/**
	  * Name of the property that contains CompanyBankAccount isOfficial
	  */
	val isOfficialAttName = "isOfficial"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains CompanyBankAccount companyId
	  */
	def companyIdColumn = table(companyIdAttName)
	
	/**
	  * Column that contains CompanyBankAccount bankId
	  */
	def bankIdColumn = table(bankIdAttName)
	
	/**
	  * Column that contains CompanyBankAccount address
	  */
	def addressColumn = table(addressAttName)
	
	/**
	  * Column that contains CompanyBankAccount creatorId
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains CompanyBankAccount created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains CompanyBankAccount deprecatedAfter
	  */
	def deprecatedAfterColumn = table(deprecatedAfterAttName)
	
	/**
	  * Column that contains CompanyBankAccount isOfficial
	  */
	def isOfficialColumn = table(isOfficialAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = CompanyBankAccountFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: CompanyBankAccountData) = 
		apply(None, Some(data.companyId), Some(data.bankId), Some(data.address), data.creatorId, 
			Some(data.created), data.deprecatedAfter, Some(data.isOfficial))
	
	override def complete(id: Value, data: CompanyBankAccountData) = CompanyBankAccount(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param address The linked bank account address
	  * @return A model containing only the specified address
	  */
	def withAddress(address: String) = apply(address = Some(address))
	
	/**
	  * @param bankId Id of the bank where the company owns an account
	  * @return A model containing only the specified bankId
	  */
	def withBankId(bankId: Int) = apply(bankId = Some(bankId))
	
	/**
	  * @param companyId Id of the company which owns this bank account
	  * @return A model containing only the specified companyId
	  */
	def withCompanyId(companyId: Int) = apply(companyId = Some(companyId))
	
	/**
	  * @param created Time when this information was registered
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user linked with this CompanyBankAccount
	  * @return A model containing only the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param deprecatedAfter Time when this CompanyBankAccount became deprecated. None while this CompanyBankAccount is still valid.
	  * @return A model containing only the specified deprecatedAfter
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = Some(deprecatedAfter))
	
	/**
	  * @param id A CompanyBankAccount id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param isOfficial Whether this bank account information was written by the company authorities
	  * @return A model containing only the specified isOfficial
	  */
	def withIsOfficial(isOfficial: Boolean) = apply(isOfficial = Some(isOfficial))
}

/**
  * Used for interacting with CompanyBankAccounts in the database
  * @param id CompanyBankAccount database id
  * @param companyId Id of the company which owns this bank account
  * @param bankId Id of the bank where the company owns an account
  * @param address The linked bank account address
  * @param creatorId Id of the user linked with this CompanyBankAccount
  * @param created Time when this information was registered
  * @param deprecatedAfter Time when this CompanyBankAccount became deprecated. None while this CompanyBankAccount is still valid.
  * @param isOfficial Whether this bank account information was written by the company authorities
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class CompanyBankAccountModel(id: Option[Int] = None, companyId: Option[Int] = None, 
	bankId: Option[Int] = None, address: Option[String] = None, creatorId: Option[Int] = None, 
	created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None, 
	isOfficial: Option[Boolean] = None) 
	extends StorableWithFactory[CompanyBankAccount]
{
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyBankAccountModel.factory
	
	override def valueProperties = 
	{
		import CompanyBankAccountModel._
		Vector("id" -> id, companyIdAttName -> companyId, bankIdAttName -> bankId, addressAttName -> address, 
			creatorIdAttName -> creatorId, createdAttName -> created, 
			deprecatedAfterAttName -> deprecatedAfter, isOfficialAttName -> isOfficial)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param address A new address
	  * @return A new copy of this model with the specified address
	  */
	def withAddress(address: String) = copy(address = Some(address))
	
	/**
	  * @param bankId A new bankId
	  * @return A new copy of this model with the specified bankId
	  */
	def withBankId(bankId: Int) = copy(bankId = Some(bankId))
	
	/**
	  * @param companyId A new companyId
	  * @return A new copy of this model with the specified companyId
	  */
	def withCompanyId(companyId: Int) = copy(companyId = Some(companyId))
	
	/**
	  * @param created A new created
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param creatorId A new creatorId
	  * @return A new copy of this model with the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = copy(creatorId = Some(creatorId))
	
	/**
	  * @param deprecatedAfter A new deprecatedAfter
	  * @return A new copy of this model with the specified deprecatedAfter
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	/**
	  * @param isOfficial A new isOfficial
	  * @return A new copy of this model with the specified isOfficial
	  */
	def withIsOfficial(isOfficial: Boolean) = copy(isOfficial = Some(isOfficial))
}

