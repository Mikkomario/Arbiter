package vf.arbiter.core.model.combined.invoice

import utopia.metropolis.model.combined.description.{DescribedFactory, DescribedWrapper, SimplyDescribed}
import utopia.metropolis.model.stored.description.{DescriptionLink, DescriptionRole}
import vf.arbiter.core.model.stored.invoice.UnitCategory

object DescribedUnitCategory extends DescribedFactory[UnitCategory, DescribedUnitCategory]

/**
  * Combines UnitCategory with the linked descriptions
  * @since 2021-10-14
  */
case class DescribedUnitCategory(wrapped: UnitCategory, descriptions: Set[DescriptionLink]) 
	extends DescribedWrapper[UnitCategory] with SimplyDescribed
{
	// IMPLEMENTED	--------------------
	
	override protected def simpleBaseModel(roles: Iterable[DescriptionRole]) = wrapped.toModel
}

