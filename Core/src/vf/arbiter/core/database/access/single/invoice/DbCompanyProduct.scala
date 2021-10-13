package vf.arbiter.core.database.access.single.invoice

import utopia.citadel.database.access.single.description.SingleIdDescribedAccess
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.database.access.many.description.DbCompanyProductDescriptions
import vf.arbiter.core.database.access.single.description.DbCompanyProductDescription
import vf.arbiter.core.database.factory.invoice.CompanyProductFactory
import vf.arbiter.core.database.model.invoice.CompanyProductModel
import vf.arbiter.core.model.combined.invoice.DescribedCompanyProduct
import vf.arbiter.core.model.stored.invoice.CompanyProduct

/**
  * Used for accessing individual CompanyProducts
  * @author Mikko Hilpinen
  * @since 2021-10-11
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
	def apply(id: Int) = new DbSingleCompanyProduct(id)
	
	
	// NESTED	--------------------
	
	// TODO: Add these features to Vault-Coder also
	class DbSingleCompanyProduct(override val id: Int)
		extends UniqueCompanyProductAccess with SingleIdDescribedAccess[CompanyProduct, DescribedCompanyProduct]
	{
		// IMPLEMENTED  --------------------
		
		override protected def singleDescriptionAccess = DbCompanyProductDescription
		
		override protected def manyDescriptionsAccess = DbCompanyProductDescriptions
		
		override protected def describedFactory = DescribedCompanyProduct
	}
}

