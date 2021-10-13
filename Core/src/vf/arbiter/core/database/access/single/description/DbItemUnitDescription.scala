package vf.arbiter.core.database.access.single.description

import utopia.citadel.database.access.single.description.DescriptionLinkAccess
import vf.arbiter.core.database.factory.CoreDescriptionLinkFactory

object DbItemUnitDescription extends DescriptionLinkAccess
{
	/**
	  * Factory used when reading ItemUnit description links
	  */
	def factory = CoreDescriptionLinkFactory.itemUnit
}

