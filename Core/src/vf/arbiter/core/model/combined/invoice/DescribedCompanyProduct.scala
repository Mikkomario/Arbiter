package vf.arbiter.core.model.combined.invoice

import utopia.metropolis.model.combined.description.{DescribedWrapper, SimplyDescribed}
import utopia.metropolis.model.stored.description.{DescriptionLink, DescriptionRole}
import vf.arbiter.core.model.stored.invoice.CompanyProduct

/**
  * Combines CompanyProduct with the linked descriptions
  * @since 2021-10-11
  */
case class DescribedCompanyProduct(wrapped: CompanyProduct, descriptions: Set[DescriptionLink]) 
	extends DescribedWrapper[CompanyProduct] with SimplyDescribed
{
	// IMPLEMENTED	--------------------
	
	override protected def simpleBaseModel(roles: Iterable[DescriptionRole]) = wrapped.toModel
}

