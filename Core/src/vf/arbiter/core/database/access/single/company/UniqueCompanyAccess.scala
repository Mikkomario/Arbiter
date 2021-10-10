package vf.arbiter.core.database.access.single.company

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.company.CompanyFactory
import vf.arbiter.core.database.model.company.CompanyModel
import vf.arbiter.core.model.stored.company.Company

/**
  * A common trait for access points that return individual and distinct Companies.
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait UniqueCompanyAccess 
	extends SingleRowModelAccess[Company] with DistinctModelAccess[Company, Option[Company], Value] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Official registration code of this company (id in the country system). None if no instance (or value) was found.
	  */
	def yCode(implicit connection: Connection) = pullColumn(model.yCodeColumn).string
	
	/**
	  * Name of this company. None if no instance (or value) was found.
	  */
	def name(implicit connection: Connection) = pullColumn(model.nameColumn).string
	
	/**
	  * Street address of this company's headquarters or operation. None if no instance (or value) was found.
	  */
	def addressId(implicit connection: Connection) = pullColumn(model.addressIdColumn).int
	
	/**
	  * Tax-related identifier code for this company. None if no instance (or value) was found.
	  */
	def taxCode(implicit connection: Connection) = pullColumn(model.taxCodeColumn).string
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the addressId of the targeted Company instance(s)
	  * @param newAddressId A new addressId to assign
	  * @return Whether any Company instance was affected
	  */
	def addressId_=(newAddressId: Int)(implicit connection: Connection) = 
		putColumn(model.addressIdColumn, newAddressId)
	
	/**
	  * Updates the name of the targeted Company instance(s)
	  * @param newName A new name to assign
	  * @return Whether any Company instance was affected
	  */
	def name_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
	
	/**
	  * Updates the taxCode of the targeted Company instance(s)
	  * @param newTaxCode A new taxCode to assign
	  * @return Whether any Company instance was affected
	  */
	def taxCode_=(newTaxCode: String)(implicit connection: Connection) = 
		putColumn(model.taxCodeColumn, newTaxCode)
	
	/**
	  * Updates the yCode of the targeted Company instance(s)
	  * @param newYCode A new yCode to assign
	  * @return Whether any Company instance was affected
	  */
	def yCode_=(newYCode: String)(implicit connection: Connection) = putColumn(model.yCodeColumn, newYCode)
}

