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
  * @since 2021-10-14
  */
trait ManyCompaniesAccess extends ManyCompaniesAccessLike[Company]
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = companyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyFactory
	
	override protected def defaultOrdering = Some(factory.defaultOrdering)
	
	override def filter(additionalCondition: Condition): ManyCompaniesAccess = 
		new ManyCompaniesAccess.ManyCompaniesSubView(this, additionalCondition)
}

