package vf.arbiter.core.model.combined.invoice

import utopia.metropolis.model.combined.description.{DescribedFactory, DescribedWrapper, LinkedDescription, SimplyDescribed}
import utopia.metropolis.model.stored.description.DescriptionRole
import vf.arbiter.core.model.stored.invoice.UnitCategory

object DescribedUnitCategory extends DescribedFactory[UnitCategory, DescribedUnitCategory]

/**
  * Combines UnitCategory with the linked descriptions
  * @param unitCategory UnitCategory to wrap
  * @param descriptions Descriptions concerning the wrapped UnitCategory
  * @since 2021-10-31
  */
case class DescribedUnitCategory(unitCategory: UnitCategory, descriptions: Set[LinkedDescription]) 
	extends DescribedWrapper[UnitCategory] with SimplyDescribed
{
	// IMPLEMENTED	--------------------
	
	override def wrapped = unitCategory
	
	override protected def simpleBaseModel(roles: Iterable[DescriptionRole]) = wrapped.toModel
}

