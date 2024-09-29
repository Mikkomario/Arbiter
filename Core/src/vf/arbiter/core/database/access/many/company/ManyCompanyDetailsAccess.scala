package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.company.CompanyDetailsFactory
import vf.arbiter.core.model.stored.company.CompanyDetails

object ManyCompanyDetailsAccess extends ViewFactory[ManyCompanyDetailsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyCompanyDetailsAccess = 
		_ManyCompanyDetailsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyCompanyDetailsAccess(override val accessCondition: Option[Condition]) 
		extends ManyCompanyDetailsAccess
}

/**
  * A common trait for access points which target multiple CompanyDetails at a time
  * @author Mikko Hilpinen
  * @since 31.10.2021
  */
trait ManyCompanyDetailsAccess 
	extends ManyCompanyDetailsAccessLike[CompanyDetails, ManyCompanyDetailsAccess] 
		with ManyRowModelAccess[CompanyDetails]
{
	// COMPUTED	--------------------
	
	/**
	  * A copy of this access point which includes full address information
	  */
	def full = {
		accessCondition match 
		{
			// Doesn't repeat the non-deprecated condition
			case Some(condition) => DbManyFullCompanyDetails.includingHistory.filter(condition)
			case None => DbManyFullCompanyDetails.includingHistory
		}
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyDetailsFactory
	
	override def self = this
	
	override def apply(condition: Condition): ManyCompanyDetailsAccess = ManyCompanyDetailsAccess(condition)
}

