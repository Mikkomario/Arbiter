package vf.arbiter.core.database.access.single.description

import utopia.citadel.database.access.single.description.LinkedDescriptionAccess
import vf.arbiter.core.database.factory.description.CoreLinkedDescriptionFactory
import vf.arbiter.core.database.model.description.CoreDescriptionLinkModel

object DbUnitCategoryDescription extends LinkedDescriptionAccess
{
	// IMPLEMENTED	--------------------
	
	override def factory = CoreLinkedDescriptionFactory.unitCategory
	
	override def linkModel = CoreDescriptionLinkModel.unitCategory
}

