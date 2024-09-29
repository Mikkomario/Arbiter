package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.company.FullCompanyDetailsFactory
import vf.arbiter.core.model.combined.company.FullCompanyDetails

object ManyFullCompanyDetailsAccess extends ViewFactory[ManyFullCompanyDetailsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyFullCompanyDetailsAccess = 
		_ManyFullCompanyDetailsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyFullCompanyDetailsAccess(override val accessCondition: Option[Condition]) 
		extends ManyFullCompanyDetailsAccess
}

/**
  * Used for accessing multiple company details at a time, including full address information
  * @author Mikko Hilpinen
  * @since 05.05.2022, v1.3
  */
trait ManyFullCompanyDetailsAccess 
	extends ManyCompanyDetailsAccessLike[FullCompanyDetails, ManyFullCompanyDetailsAccess] 
		with ManyRowModelAccess[FullCompanyDetails]
{
	// IMPLEMENTED	--------------------
	
	override def factory = FullCompanyDetailsFactory
	
	override def self = this
	
	override def apply(condition: Condition): ManyFullCompanyDetailsAccess = 
		ManyFullCompanyDetailsAccess(condition)
}

