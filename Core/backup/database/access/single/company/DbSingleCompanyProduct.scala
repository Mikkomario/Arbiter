package vf.arbiter.core.database.access.single.company

import utopia.citadel.database.access.single.description.SingleIdDescribedAccess
import vf.arbiter.core.database.access.many.description.DbCompanyProductDescriptions
import vf.arbiter.core.database.access.single.description.DbCompanyProductDescription
import vf.arbiter.core.model.combined.company.DescribedCompanyProduct
import vf.arbiter.core.model.stored.company.CompanyProduct

/**
  * An access point to individual CompanyProducts, based on their id
  * @since 2021-10-14
  */
case class DbSingleCompanyProduct(id: Int) 
	extends UniqueCompanyProductAccess with SingleIdDescribedAccess[CompanyProduct, DescribedCompanyProduct]
{
	// IMPLEMENTED	--------------------
	
	override protected def describedFactory = DescribedCompanyProduct
	
	override protected def manyDescriptionsAccess = DbCompanyProductDescriptions
	
	override protected def singleDescriptionAccess = DbCompanyProductDescription
}

