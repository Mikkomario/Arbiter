package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.company.CompanyDetailsFactory
import vf.arbiter.core.model.stored.company.CompanyDetails

object ManyCompanyDetailsAccess
{
	// NESTED	--------------------
	
	private class ManyCompanyDetailsSubView(override val parent: ManyRowModelAccess[CompanyDetails], 
		override val filterCondition: Condition) 
		extends ManyCompanyDetailsAccess with SubView
}

/**
  * A common trait for access points which target multiple CompanyDetails at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait ManyCompanyDetailsAccess
	extends ManyCompanyDetailsAccessLike[CompanyDetails, ManyCompanyDetailsAccess]
		with ManyRowModelAccess[CompanyDetails]
{
	// COMPUTED ------------------------
	
	/**
	 * @return A copy of this access point which includes full address information
	 */
	def full = accessCondition match {
		// Doesn't repeat the non-deprecated condition
		case Some(condition) => DbManyFullCompanyDetails.includingHistory.filter(condition)
		case None => DbManyFullCompanyDetails.includingHistory
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override def factory = CompanyDetailsFactory
	
	override def filter(additionalCondition: Condition): ManyCompanyDetailsAccess = 
		new ManyCompanyDetailsAccess.ManyCompanyDetailsSubView(this, additionalCondition)
}

