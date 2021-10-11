package vf.arbiter.core.database.access.many.description

import utopia.citadel.database.access.many.description.DbDescriptions.{DescriptionsOfAll, DescriptionsOfMany, DescriptionsOfSingle}
import vf.arbiter.core.database.factory.CoreDescriptionLinkFactory
import vf.arbiter.core.database.model.CoreDescriptionLinkModel

object DbCompanyProductDescriptions
{
	// ATTRIBUTES	--------------------
	
	/**
	  * An access point to the descriptions of all CompanyProducts at once
	  */
	val all = DescriptionsOfAll(factory, model)
	
	
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
	  * @return An access point to that CompanyProduct's descriptions
	  */
	def apply(productId: Int) = DescriptionsOfSingle(productId, factory, model)
	
	/**
	  * @param productIds Ids of the CompanyProducts to target
	  * @return An access point to descriptions of the targeted CompanyProducts
	  */
	def apply(productIds: Set[Int]) = DescriptionsOfMany(productIds, factory, model)
}

