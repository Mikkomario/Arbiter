package vf.arbiter.core.database.access.many.company

import utopia.citadel.database.access.many.description.ManyDescribedAccessByIds
import utopia.vault.nosql.view.NonDeprecatedView
import vf.arbiter.core.model.combined.company.DescribedCompanyProduct
import vf.arbiter.core.model.stored.company.CompanyProduct

/**
  * The root access point when targeting multiple CompanyProducts at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbCompanyProducts extends ManyCompanyProductsAccess with NonDeprecatedView[CompanyProduct]
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted CompanyProducts
	  * @return An access point to CompanyProducts with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbCompanyProductsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbCompanyProductsSubset(override val ids: Set[Int]) 
		extends ManyCompanyProductsAccess 
			with ManyDescribedAccessByIds[CompanyProduct, DescribedCompanyProduct]
}

