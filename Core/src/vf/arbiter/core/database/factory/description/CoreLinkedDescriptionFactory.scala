package vf.arbiter.core.database.factory.description

import utopia.citadel.database.factory.description.LinkedDescriptionFactory

object CoreLinkedDescriptionFactory
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Factory for reading descriptions linked with CompanyProducts
	  */
	lazy val companyProduct = LinkedDescriptionFactory(CoreDescriptionLinkFactory.companyProduct)
	
	/**
	  * Factory for reading descriptions linked with ItemUnits
	  */
	lazy val itemUnit = LinkedDescriptionFactory(CoreDescriptionLinkFactory.itemUnit)
	
	/**
	  * Factory for reading descriptions linked with UnitCategories
	  */
	lazy val unitCategory = LinkedDescriptionFactory(CoreDescriptionLinkFactory.unitCategory)
}

