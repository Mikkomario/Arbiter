package vf.arbiter.core.database.model.description

import utopia.citadel.database.model.description.DescriptionLinkModelFactory
import vf.arbiter.core.database.CoreTables

object CoreDescriptionLinkModel
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Database interaction model factory for CompanyProduct description links
	  */
	lazy val companyProduct = DescriptionLinkModelFactory(CoreTables.companyProductDescription)
	
	/**
	  * Database interaction model factory for ItemUnit description links
	  */
	lazy val itemUnit = DescriptionLinkModelFactory(CoreTables.itemUnitDescription)
	
	/**
	  * Database interaction model factory for UnitCategory description links
	  */
	lazy val unitCategory = DescriptionLinkModelFactory(CoreTables.unitCategoryDescription)
}

