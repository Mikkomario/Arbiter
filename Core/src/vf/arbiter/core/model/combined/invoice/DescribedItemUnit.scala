package vf.arbiter.core.model.combined.invoice

import utopia.metropolis.model.combined.description.{DescribedFactory, DescribedWrapper, SimplyDescribed}
import utopia.metropolis.model.stored.description.{DescriptionLink, DescriptionRole}
import vf.arbiter.core.model.stored.invoice.ItemUnit

object DescribedItemUnit extends DescribedFactory[ItemUnit, DescribedItemUnit]

/**
  * Combines ItemUnit with the linked descriptions
  * @since 2021-10-14
  */
case class DescribedItemUnit(itemUnit: ItemUnit, descriptions: Set[DescriptionLink])
	extends DescribedWrapper[ItemUnit] with SimplyDescribed
{
	// IMPLEMENTED	--------------------
	
	override def wrapped = itemUnit
	
	override protected def simpleBaseModel(roles: Iterable[DescriptionRole]) = itemUnit.toModel
}

