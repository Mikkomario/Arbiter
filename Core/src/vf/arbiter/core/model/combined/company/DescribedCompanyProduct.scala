package vf.arbiter.core.model.combined.company

import utopia.metropolis.model.combined.description.{DescribedFactory, DescribedWrapper, SimplyDescribed}
import utopia.metropolis.model.stored.description.{DescriptionLink, DescriptionRole}
import vf.arbiter.core.model.stored.company.CompanyProduct

object DescribedCompanyProduct extends DescribedFactory[CompanyProduct, DescribedCompanyProduct]

/**
  * Combines CompanyProduct with the linked descriptions
  * @since 2021-10-14
  */
case class DescribedCompanyProduct(wrapped: CompanyProduct, descriptions: Set[DescriptionLink]) 
	extends DescribedWrapper[CompanyProduct] with SimplyDescribed
{
	// IMPLEMENTED	--------------------
	
	override protected def simpleBaseModel(roles: Iterable[DescriptionRole]) = wrapped.toModel
}

