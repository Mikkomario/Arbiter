package vf.arbiter.core.database.access.single.invoice

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.database.access.many.description.DbCompanyProductDescriptions
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
	
	class DbSingleCompanyProduct(val id: Int) 
		extends UniqueCompanyProductAccess with UniqueModelAccess[CompanyProduct]
	{
		// COMPUTED ------------------------
		
		// TODO: Add to Vault-Coder (these can form a trait, probably)
		def descriptions = DbCompanyProductDescriptions(id)
		// TODO: Add to Vault-Coder (under distinct single trait)
		def described(implicit connection: Connection) = pull.map { product =>
			DescribedCompanyProduct(product, descriptions.toSet)
		}
		
		
		// OTHER    ------------------------
		
		// TODO: This kind of methods should be added to Vault-Coder, but only after DescriptionRole
		//  trait support is added back
		def descriptionsOfRole(roleId: Int)(implicit connection: Connection) =
			descriptions.forRoleWithId(roleId)
		
		
		// IMPLEMENTED	--------------------
		
		override def condition = index <=> id
	}
}

