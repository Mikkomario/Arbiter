package vf.arbiter.core.database.access.many.company

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import vf.arbiter.core.database.access.many.company.ManyCompaniesAccess.CompaniesSubView
import vf.arbiter.core.database.factory.company.CompanyFactory
import vf.arbiter.core.database.model.company.CompanyModel
import vf.arbiter.core.model.stored.company.Company

object ManyCompaniesAccess
{
	// NESTED   -----------------------------------
	
	private class CompaniesSubView(override val parent: ManyRowModelAccess[Company],
	                               override val filterCondition: Condition)
		extends ManyCompaniesAccess with SubView
}

/**
  * A common trait for access points which target multiple Companies at a time
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait ManyCompaniesAccess extends ManyRowModelAccess[Company] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * yCodes of the accessible Companies
	  */
	def yCodes(implicit connection: Connection) = pullColumn(model.yCodeColumn)
		.flatMap { value => value.string }
	/**
	  * names of the accessible Companies
	  */
	def names(implicit connection: Connection) = pullColumn(model.nameColumn)
		.flatMap { value => value.string }
	/**
	  * addressIds of the accessible Companies
	  */
	def addressIds(implicit connection: Connection) = 
		pullColumn(model.addressIdColumn).flatMap { value => value.int }
	/**
	  * taxCodes of the accessible Companies
	  */
	def taxCodes(implicit connection: Connection) = 
		pullColumn(model.taxCodeColumn).flatMap { value => value.string }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyFactory
	
	override protected def defaultOrdering = None
	
	override def filter(additionalCondition: Condition): ManyCompaniesAccess =
		new CompaniesSubView(this, additionalCondition)
	
	
	// OTHER	--------------------
	
	/**
	 * Finds companies within this group that contain the specified string in their name
	 * @param companyNamePart String that must be contained within a company name
	 * @param connection Implicit DB Connection
	 * @return Companies that have the specified string in their name
	 */
	def matchingName(companyNamePart: String)(implicit connection: Connection) =
		find(model.nameColumn.contains(companyNamePart))
	
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

