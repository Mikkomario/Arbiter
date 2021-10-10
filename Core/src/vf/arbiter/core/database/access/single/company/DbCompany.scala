package vf.arbiter.core.database.access.single.company

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.company.CompanyFactory
import vf.arbiter.core.database.model.company.CompanyModel
import vf.arbiter.core.model.stored.company.Company

/**
  * Used for accessing individual Companys
  * @author Mikko Hilpinen
  * @since 2021-10-10
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
	def apply(id: Int) = new DbSingleCompany(id)
	
	
	// NESTED	--------------------
	
	class DbSingleCompany(val id: Int) extends UniqueCompanyAccess with UniqueModelAccess[Company]
	{
		// IMPLEMENTED	--------------------
		
		override def condition = index <=> id
	}
}

