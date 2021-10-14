package vf.arbiter.core.database.access.single.company

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.database.factory.company.CompanyProductFactory
import vf.arbiter.core.database.model.company.CompanyProductModel
import vf.arbiter.core.model.stored.company.CompanyProduct

/**
  * Used for accessing individual CompanyProducts
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object DbCompanyProduct 
	extends SingleRowModelAccess[CompanyProduct] with NonDeprecatedView[CompanyProduct] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyProductModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyProductFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted CompanyProduct instance
	  * @return An access point to that CompanyProduct
	  */
	def apply(id: Int) = DbSingleCompanyProduct(id)
}

