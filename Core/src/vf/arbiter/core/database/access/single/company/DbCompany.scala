package vf.arbiter.core.database.access.single.company

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.company.CompanyFactory
import vf.arbiter.core.database.model.company.{CompanyDetailsModel, CompanyModel}
import vf.arbiter.core.model.partial.company.{CompanyData, CompanyDetailsData}
import vf.arbiter.core.model.stored.company.Company

/**
  * Used for accessing individual Companies
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbCompany extends SingleRowModelAccess[Company] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted Company instance
	  * @return An access point to that Company
	  */
	def apply(id: Int) = DbSingleCompany(id)
	
	/**
	 * Inserts a new company to the database
	 * @param yCode Y-code of the company
	 * @param name Name of the company
	 * @param addressId Id of the company's address
	 * @param taxCode Tax code of the company (optional)
	 * @param creatorId Id of the user who provided this data (optional)
	 * @param connection Implicit DB Connection
	 * @return Inserted company
	 */
	def insert(yCode: String, name: String, addressId: Int, taxCode: Option[String] = None,
	           creatorId: Option[Int] = None)(implicit connection: Connection) =
	{
		val company = model.insert(CompanyData(yCode, creatorId))
		val details = CompanyDetailsModel.insert(CompanyDetailsData(company.id, name, addressId, taxCode, creatorId))
		company + details
	}
}

