package vf.arbiter.core.database.model.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.company.CompanyBankAddressFactory
import vf.arbiter.core.model.partial.company.CompanyBankAddressData
import vf.arbiter.core.model.stored.company.CompanyBankAddress

/**
  * Used for constructing CompanyBankAddressModel instances and for inserting CompanyBankAddresss to the database
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object CompanyBankAddressModel 
	extends DataInserter[CompanyBankAddressModel, CompanyBankAddress, CompanyBankAddressData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains CompanyBankAddress companyId
	  */
	val companyIdAttName = "companyId"
	
	/**
	  * Name of the property that contains CompanyBankAddress bankId
	  */
	val bankIdAttName = "bankId"
	
	/**
	  * Name of the property that contains CompanyBankAddress address
	  */
	val addressAttName = "address"
	
	/**
	  * Name of the property that contains CompanyBankAddress created
	  */
	val createdAttName = "created"
	
	/**
	  * Name of the property that contains CompanyBankAddress isDefault
	  */
	val isDefaultAttName = "isDefault"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains CompanyBankAddress companyId
	  */
	def companyIdColumn = table(companyIdAttName)
	
	/**
	  * Column that contains CompanyBankAddress bankId
	  */
	def bankIdColumn = table(bankIdAttName)
	
	/**
	  * Column that contains CompanyBankAddress address
	  */
	def addressColumn = table(addressAttName)
	
	/**
	  * Column that contains CompanyBankAddress created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains CompanyBankAddress isDefault
	  */
	def isDefaultColumn = table(isDefaultAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = CompanyBankAddressFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: CompanyBankAddressData) = 
		apply(None, Some(data.companyId), Some(data.bankId), Some(data.address), Some(data.created), 
			Some(data.isDefault))
	
	override def complete(id: Value, data: CompanyBankAddressData) = CompanyBankAddress(id.getInt, data)
	
	
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
	  * @param companyId Id of the company which owns this bank account address
	  * @return A model containing only the specified companyId
	  */
	def withCompanyId(companyId: Int) = apply(companyId = Some(companyId))
	
	/**
	  * @param created Time when this information was registered
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A CompanyBankAddress id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param isDefault Whether this is the preferred / primary bank address used by this company
	  * @return A model containing only the specified isDefault
	  */
	def withIsDefault(isDefault: Boolean) = apply(isDefault = Some(isDefault))
}

/**
  * Used for interacting with CompanyBankAddresses in the database
  * @param id CompanyBankAddress database id
  * @param companyId Id of the company which owns this bank account address
  * @param bankId Id of the bank where the company owns an account
  * @param address The linked bank account address
  * @param created Time when this information was registered
  * @param isDefault Whether this is the preferred / primary bank address used by this company
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class CompanyBankAddressModel(id: Option[Int] = None, companyId: Option[Int] = None, 
	bankId: Option[Int] = None, address: Option[String] = None, created: Option[Instant] = None, 
	isDefault: Option[Boolean] = None) 
	extends StorableWithFactory[CompanyBankAddress]
{
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyBankAddressModel.factory
	
	override def valueProperties = 
	{
		import CompanyBankAddressModel._
		Vector("id" -> id, companyIdAttName -> companyId, bankIdAttName -> bankId, addressAttName -> address, 
			createdAttName -> created, isDefaultAttName -> isDefault)
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
	  * @param isDefault A new isDefault
	  * @return A new copy of this model with the specified isDefault
	  */
	def withIsDefault(isDefault: Boolean) = copy(isDefault = Some(isDefault))
}

