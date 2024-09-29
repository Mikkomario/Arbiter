package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.company.CompanyFactory
import vf.arbiter.core.model.stored.company.Company

object ManyCompaniesAccess extends ViewFactory[ManyCompaniesAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyCompaniesAccess = _ManyCompaniesAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyCompaniesAccess(override val accessCondition: Option[Condition]) 
		extends ManyCompaniesAccess
}

/**
  * A common trait for access points which target multiple Companies at a time
  * @author Mikko Hilpinen
  * @since 31.10.2021
  */
trait ManyCompaniesAccess extends ManyCompaniesAccessLike[Company, ManyCompaniesAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * A copy of this access point which includes latest details for each accessible company
	  */
	def detailed = {
		accessCondition match 
		{
			case Some(c) => DbDetailedCompanies.filter(c)
			case None => DbDetailedCompanies
		}
	}
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = companyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyFactory
	
	override def self = this
	
	override def apply(condition: Condition): ManyCompaniesAccess = ManyCompaniesAccess(condition)
}

