package vf.arbiter.core.database.model.company

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.company.CompanyFactory
import vf.arbiter.core.model.partial.company.CompanyData
import vf.arbiter.core.model.stored.company.Company

/**
  * Used for constructing CompanyModel instances and for inserting Companys to the database
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object CompanyModel extends DataInserter[CompanyModel, Company, CompanyData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains Company yCode
	  */
	val yCodeAttName = "yCode"
	
	/**
	  * Name of the property that contains Company name
	  */
	val nameAttName = "name"
	
	/**
	  * Name of the property that contains Company addressId
	  */
	val addressIdAttName = "addressId"
	
	/**
	  * Name of the property that contains Company taxCode
	  */
	val taxCodeAttName = "taxCode"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains Company yCode
	  */
	def yCodeColumn = table(yCodeAttName)
	
	/**
	  * Column that contains Company name
	  */
	def nameColumn = table(nameAttName)
	
	/**
	  * Column that contains Company addressId
	  */
	def addressIdColumn = table(addressIdAttName)
	
	/**
	  * Column that contains Company taxCode
	  */
	def taxCodeColumn = table(taxCodeAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = CompanyFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: CompanyData) = 
		apply(None, Some(data.yCode), Some(data.name), Some(data.addressId), data.taxCode)
	
	override def complete(id: Value, data: CompanyData) = Company(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param addressId Street address of this company's headquarters or operation
	  * @return A model containing only the specified addressId
	  */
	def withAddressId(addressId: Int) = apply(addressId = Some(addressId))
	
	/**
	  * @param id A Company id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param name Name of this company
	  * @return A model containing only the specified name
	  */
	def withName(name: String) = apply(name = Some(name))
	
	/**
	  * @param taxCode Tax-related identifier code for this company
	  * @return A model containing only the specified taxCode
	  */
	def withTaxCode(taxCode: String) = apply(taxCode = Some(taxCode))
	
	/**
	  * @param yCode Official registration code of this company (id in the country system)
	  * @return A model containing only the specified yCode
	  */
	def withYCode(yCode: String) = apply(yCode = Some(yCode))
}

/**
  * Used for interacting with Companys in the database
  * @param id Company database id
  * @param yCode Official registration code of this company (id in the country system)
  * @param name Name of this company
  * @param addressId Street address of this company's headquarters or operation
  * @param taxCode Tax-related identifier code for this company
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class CompanyModel(id: Option[Int] = None, yCode: Option[String] = None, name: Option[String] = None, 
	addressId: Option[Int] = None, taxCode: Option[String] = None) 
	extends StorableWithFactory[Company]
{
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyModel.factory
	
	override def valueProperties = 
	{
		import CompanyModel._
		Vector("id" -> id, yCodeAttName -> yCode, nameAttName -> name, addressIdAttName -> addressId, 
			taxCodeAttName -> taxCode)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param addressId A new addressId
	  * @return A new copy of this model with the specified addressId
	  */
	def withAddressId(addressId: Int) = copy(addressId = Some(addressId))
	
	/**
	  * @param name A new name
	  * @return A new copy of this model with the specified name
	  */
	def withName(name: String) = copy(name = Some(name))
	
	/**
	  * @param taxCode A new taxCode
	  * @return A new copy of this model with the specified taxCode
	  */
	def withTaxCode(taxCode: String) = copy(taxCode = Some(taxCode))
	
	/**
	  * @param yCode A new yCode
	  * @return A new copy of this model with the specified yCode
	  */
	def withYCode(yCode: String) = copy(yCode = Some(yCode))
}

