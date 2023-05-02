package vf.arbiter.command.database.access.many.environment

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple DescriptionImports at a time
  * @author Mikko Hilpinen
  * @since 2021-10-20
  */
object DbDescriptionImports extends ManyDescriptionImportsAccess with UnconditionalView
{
	/**
	 * @param ids Description import ids
	 * @return An access point to those imports
	 */
	def apply(ids: Iterable[Int]) = filter(index in ids)
}