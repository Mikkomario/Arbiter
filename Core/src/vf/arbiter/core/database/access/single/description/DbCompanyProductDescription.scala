package vf.arbiter.core.database.access.single.description

import utopia.citadel.database.access.single.description.LinkedDescriptionAccess
import vf.arbiter.core.database.factory.description.CoreLinkedDescriptionFactory
import vf.arbiter.core.database.model.description.CoreDescriptionLinkModel

object DbCompanyProductDescription extends LinkedDescriptionAccess
{
	// IMPLEMENTED	--------------------
	
	override def factory = CoreLinkedDescriptionFactory.companyProduct
	
	override def linkModel = CoreDescriptionLinkModel.companyProduct
}

