package vf.arbiter.core.database.factory.description

import utopia.citadel.database.factory.description.DescriptionLinkFactory
import vf.arbiter.core.database.CoreTables

object CoreDescriptionLinkFactory
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Factory for reading CompanyProduct description links
	  */
	lazy val companyProduct = DescriptionLinkFactory(CoreTables.companyProductDescription)
	
	/**
	  * Factory for reading ItemUnit description links
	  */
	lazy val itemUnit = DescriptionLinkFactory(CoreTables.itemUnitDescription)
	
	/**
	  * Factory for reading UnitCategory description links
	  */
	lazy val unitCategory = DescriptionLinkFactory(CoreTables.unitCategoryDescription)
}

