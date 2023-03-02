package vf.arbiter.core.model.combined.invoice

import utopia.citadel.model.enumeration.CitadelDescriptionRole.Name
import utopia.flow.util.StringExtensions._
import utopia.metropolis.model.combined.description.{DescribedFactory, DescribedWrapper, LinkedDescription, SimplyDescribed}
import utopia.metropolis.model.stored.description.DescriptionRole
import vf.arbiter.core.model.enumeration.ArbiterDescriptionRoleId.Abbreviation
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
	// COMPUTED ------------------------
	
	/**
	 * @return Id of this unit
	 */
	def id = itemUnit.id
	/**
	 * @return Name of this unit
	 */
	def name =  apply(Name, Abbreviation).nonEmptyOrElse(s"Unnamed unit $id")
	/**
	 * @return An abbreviation for this unit (name if no abbreviation is available)
	 */
	def abbreviation = apply(Abbreviation, Name).nonEmptyOrElse("unit")
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = itemUnit
	
	override protected def simpleBaseModel(roles: Iterable[DescriptionRole]) = wrapped.toModel
}

