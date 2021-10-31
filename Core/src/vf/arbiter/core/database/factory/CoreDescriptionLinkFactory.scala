package vf.arbiter.core.database.factory

import utopia.citadel.database.factory.description.DescriptionLinkFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.database.model.CoreDescriptionLinkModel

object CoreDescriptionLinkFactory
{
	// ATTRIBUTES	--------------------
	
	val companyProduct = DescriptionLinkFactory(CoreTables.companyProductDescription)
	
	val itemUnit = DescriptionLinkFactory(CoreTables.itemUnitDescription)
	
	val unitCategory = DescriptionLinkFactory(CoreTables.unitCategoryDescription)
}

