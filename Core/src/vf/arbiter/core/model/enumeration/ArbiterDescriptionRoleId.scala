package vf.arbiter.core.model.enumeration

import utopia.citadel.model.enumeration.StandardDescriptionRoleId

/**
 * Used for accessing description role ids for the Arbiter project
 * @author Mikko Hilpinen
 * @since 11.10.2021, v0.1
 */
object ArbiterDescriptionRoleId
{
	/**
	 * Id of the abbreviation description role
	 */
	val abbreviation = 2
	
	/**
	 * @return Id of the name description role
	 */
	def name = StandardDescriptionRoleId.name
}
