package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.company.CompanyFactory
import vf.arbiter.core.model.stored.company.Company

object ManyCompaniesAccess
{
	// NESTED	--------------------
	
	private class ManyCompaniesSubView(override val parent: ManyRowModelAccess[Company], 
		override val filterCondition: Condition) 
		extends ManyCompaniesAccess with SubView
}

/**
  * A common trait for access points which target multiple Companies at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait ManyCompaniesAccess extends ManyCompaniesAccessLike[Company, ManyCompaniesAccess]
{
	// COMPUTED	--------------------
	
	/**
	 * Factory used for constructing database the interaction models
	 */
	protected def model = companyModel
	
	/**
	 * @return A copy of this access point which includes latest details for each accessible company
	 */
	def detailed = accessCondition match {
		case Some(c) => DbDetailedCompanies.filter(c)
		case None => DbDetailedCompanies
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override def factory = CompanyFactory
	
	override def filter(additionalCondition: Condition): ManyCompaniesAccess =
		new ManyCompaniesAccess.ManyCompaniesSubView(this, additionalCondition)
}

