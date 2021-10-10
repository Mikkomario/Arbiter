package vf.arbiter.core.database.access.single.company

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.company.OrganizationCompanyFactory
import vf.arbiter.core.database.model.company.OrganizationCompanyModel
import vf.arbiter.core.model.stored.company.OrganizationCompany

/**
  * Used for accessing individual OrganizationCompanys
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object DbOrganizationCompany 
	extends SingleRowModelAccess[OrganizationCompany] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = OrganizationCompanyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = OrganizationCompanyFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted OrganizationCompany instance
	  * @return An access point to that OrganizationCompany
	  */
	def apply(id: Int) = new DbSingleOrganizationCompany(id)
	
	
	// NESTED	--------------------
	
	class DbSingleOrganizationCompany(val id: Int) 
		extends UniqueOrganizationCompanyAccess with UniqueModelAccess[OrganizationCompany]
	{
		// IMPLEMENTED	--------------------
		
		override def condition = index <=> id
	}
}

