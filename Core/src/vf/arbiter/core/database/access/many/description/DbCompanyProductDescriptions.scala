package vf.arbiter.core.database.access.many.description

import utopia.citadel.database.access.many.description.DescriptionLinksAccess
import vf.arbiter.core.database.factory.CoreDescriptionLinkFactory

object DbCompanyProductDescriptions extends DescriptionLinksAccess
{
	/**
	  * Factory used when reading CompanyProduct description links
	  */
	override def factory = CoreDescriptionLinkFactory.companyProduct
}

