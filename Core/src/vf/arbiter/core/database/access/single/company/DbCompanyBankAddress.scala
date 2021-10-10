package vf.arbiter.core.database.access.single.company

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.company.CompanyBankAddressFactory
import vf.arbiter.core.database.model.company.CompanyBankAddressModel
import vf.arbiter.core.model.stored.company.CompanyBankAddress

/**
  * Used for accessing individual CompanyBankAddresses
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object DbCompanyBankAddress 
	extends SingleRowModelAccess[CompanyBankAddress] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyBankAddressModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyBankAddressFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted CompanyBankAddress instance
	  * @return An access point to that CompanyBankAddress
	  */
	def apply(id: Int) = new DbSingleCompanyBankAddress(id)
	
	
	// NESTED	--------------------
	
	class DbSingleCompanyBankAddress(val id: Int) 
		extends UniqueCompanyBankAddressAccess with UniqueModelAccess[CompanyBankAddress]
	{
		// IMPLEMENTED	--------------------
		
		override def condition = index <=> id
	}
}

