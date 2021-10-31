package vf.arbiter.core.model.combined.invoice

import utopia.metropolis.model.combined.description.{DescribedFactory, DescribedWrapper, LinkedDescription, SimplyDescribed}
import utopia.metropolis.model.stored.description.DescriptionRole
import vf.arbiter.core.model.stored.invoice.ItemUnit

object DescribedItemUnit extends DescribedFactory[ItemUnit, DescribedItemUnit]

/**
  * Combines ItemUnit with the linked descriptions
  * @param itemUnit ItemUnit to wrap
  * @param descriptions Descriptions concerning the wrapped ItemUnit
  * @since 2021-10-31
  */
case class DescribedItemUnit(itemUnit: ItemUnit, descriptions: Set[LinkedDescription]) 
	extends DescribedWrapper[ItemUnit] with SimplyDescribed
{
	// IMPLEMENTED	--------------------
	
	override def wrapped = itemUnit
	
	override protected def simpleBaseModel(roles: Iterable[DescriptionRole]) = wrapped.toModel
}

