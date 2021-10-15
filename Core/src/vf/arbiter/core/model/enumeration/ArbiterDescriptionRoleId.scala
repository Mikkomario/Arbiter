package vf.arbiter.core.model.enumeration

import utopia.metropolis.model.enumeration.DescriptionRoleIdWrapper

/**
 * Used for accessing description role ids for the Arbiter project
 * @author Mikko Hilpinen
 * @since 11.10.2021, v0.1
 */
object ArbiterDescriptionRoleId
{
	case object Abbreviation extends DescriptionRoleIdWrapper
	{
		override val id = 2
	}
}
