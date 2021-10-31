package vf.arbiter.core.database.access.single.company

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.database.access.many.company.DbManyCompanyDetails
import vf.arbiter.core.database.factory.company.CompanyDetailsFactory
import vf.arbiter.core.database.model.company.CompanyDetailsModel
import vf.arbiter.core.model.stored.company.CompanyDetails

/**
  * Used for accessing individual CompanyDetails
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbCompanyDetails 
	extends SingleRowModelAccess[CompanyDetails] with NonDeprecatedView[CompanyDetails] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyDetailsModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyDetailsFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted CompanyDetails instance
	  * @return An access point to that CompanyDetails
	  */
	def apply(id: Int) = DbSingleCompanyDetails(id)
	
	/**
	 * Finds a company with the specified name (exact, but case-insensitive)
	 * @param companyName A company name
	 * @param connection Implicit DB Connection
	 * @return Company with that name, if found
	 */
	def withName(companyName: String)(implicit connection: Connection) =
		find(model.withName(companyName).toCondition)
	
	/**
	 * Finds a company that contains the specified string in its name
	 * @param companyNamePart A string searched from company name
	 * @param connection Implicit DB Connection
	 * @return A company containing that string in its name. If multiple results are found, the shortest company
	 *         name is selected
	 */
	def matchingName(companyNamePart: String)(implicit connection: Connection) =
		DbManyCompanyDetails.matchingName(companyNamePart).minByOption { _.name.length }
}

