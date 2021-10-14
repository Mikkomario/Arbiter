package vf.arbiter.core.database.access.many.description

import utopia.citadel.database.access.many.description.DescriptionLinksAccess
import vf.arbiter.core.database.factory.CoreDescriptionLinkFactory

object DbUnitCategoryDescriptions extends DescriptionLinksAccess
{
	// IMPLEMENTED	--------------------
	
	override def factory = CoreDescriptionLinkFactory.unitCategory
}

