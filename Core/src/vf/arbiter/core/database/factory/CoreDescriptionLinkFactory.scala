package vf.arbiter.core.database.factory

import utopia.citadel.database.factory.description.DescriptionLinkFactory
import vf.arbiter.core.database.model.CoreDescriptionLinkModel

object CoreDescriptionLinkFactory
{
	// ATTRIBUTES	--------------------
	
	val companyProduct = DescriptionLinkFactory(CoreDescriptionLinkModel.companyProduct)
	
	val itemUnit = DescriptionLinkFactory(CoreDescriptionLinkModel.itemUnit)
}

