package vf.arbiter.core.model.combined.invoice

import utopia.metropolis.model.combined.description.{DescribedWrapper, SimplyDescribed}
import utopia.metropolis.model.stored.description.{DescriptionLink, DescriptionRole}
import vf.arbiter.core.model.stored.invoice.ItemUnit

/**
  * Combines ItemUnit with the linked descriptions
  * @since 2021-10-10
  */
@deprecated("Please refer to item unit ids and descriptions instead", "v0.1")
case class DescribedItemUnit(wrapped: ItemUnit, descriptions: Set[DescriptionLink]) 
	extends DescribedWrapper[ItemUnit] with SimplyDescribed
{
	// IMPLEMENTED	--------------------
	
	override protected def simpleBaseModel(roles: Iterable[DescriptionRole]) = wrapped.toModel
}

