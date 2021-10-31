package vf.arbiter.core.database.model

import utopia.citadel.database.model.description.DescriptionLinkModelFactory
import vf.arbiter.core.database.CoreTables

object CoreDescriptionLinkModel
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Database interaction model factory for CompanyProduct description links
	  */
	val companyProduct = DescriptionLinkModelFactory(CoreTables.companyProductDescription)
	
	/**
	  * Database interaction model factory for ItemUnit description links
	  */
	val itemUnit = DescriptionLinkModelFactory(CoreTables.itemUnitDescription)
	
	/**
	  * Database interaction model factory for UnitCategory description links
	  */
	val unitCategory = DescriptionLinkModelFactory(CoreTables.unitCategoryDescription)
}

