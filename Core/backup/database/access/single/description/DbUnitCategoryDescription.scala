package vf.arbiter.core.database.access.single.description

import utopia.citadel.database.access.single.description.DescriptionLinkAccess
import vf.arbiter.core.database.factory.CoreDescriptionLinkFactory

object DbUnitCategoryDescription extends DescriptionLinkAccess
{
	// IMPLEMENTED	--------------------
	
	override def factory = CoreDescriptionLinkFactory.unitCategory
}

