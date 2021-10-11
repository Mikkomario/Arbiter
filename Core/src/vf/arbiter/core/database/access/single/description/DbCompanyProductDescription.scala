package vf.arbiter.core.database.access.single.description

import utopia.citadel.database.access.single.description.DbDescription.DescriptionOfSingle
import vf.arbiter.core.database.factory.CoreDescriptionLinkFactory
import vf.arbiter.core.database.model.CoreDescriptionLinkModel

object DbCompanyProductDescription
{
	// COMPUTED	--------------------
	
	/**
	  * Model factory used when interacting with CompanyProduct description links
	  */
	def model = CoreDescriptionLinkModel.companyProduct
	
	/**
	  * Factory used when reading CompanyProduct description links
	  */
	def factory = CoreDescriptionLinkFactory.companyProduct
	
	
	// OTHER	--------------------
	
	/**
	  * @param productId Id of the targeted CompanyProduct
	  * @return Access point to the targeted CompanyProduct's individual descriptions
	  */
	def apply(productId: Int) = DescriptionOfSingle(productId, factory, model)
}

