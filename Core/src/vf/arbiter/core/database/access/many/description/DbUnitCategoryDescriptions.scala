package vf.arbiter.core.database.access.many.description

import utopia.citadel.database.access.many.description.LinkedDescriptionsAccess
import vf.arbiter.core.database.factory.description.CoreLinkedDescriptionFactory
import vf.arbiter.core.database.model.description.CoreDescriptionLinkModel

object DbUnitCategoryDescriptions extends LinkedDescriptionsAccess
{
	// IMPLEMENTED	--------------------
	
	override def factory = CoreLinkedDescriptionFactory.unitCategory
	
	override def linkModel = CoreDescriptionLinkModel.unitCategory
}

