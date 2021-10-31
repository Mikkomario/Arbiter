package vf.arbiter.core.database.access.single.description

import utopia.citadel.database.access.single.description.LinkedDescriptionAccess
import vf.arbiter.core.database.factory.description.CoreLinkedDescriptionFactory
import vf.arbiter.core.database.model.description.CoreDescriptionLinkModel

object DbItemUnitDescription extends LinkedDescriptionAccess
{
	// IMPLEMENTED	--------------------
	
	override def factory = CoreLinkedDescriptionFactory.itemUnit
	
	override def linkModel = CoreDescriptionLinkModel.itemUnit
}

