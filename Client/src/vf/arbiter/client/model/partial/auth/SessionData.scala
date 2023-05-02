package vf.arbiter.client.model.partial.auth

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object SessionData extends FromModelFactoryWithSchema[SessionData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("userId", IntType, Vector("user_id")), 
			PropertyDeclaration("created", InstantType, isOptional = true), 
			PropertyDeclaration("deprecatedAfter", InstantType, Vector("deprecated_after"), 
			isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		SessionData(valid("userId").getInt, valid("created").getInstant, valid("deprecatedAfter").instant)
}

/**
  * Represents a local user-session
  * @param userId Id of the logged-in user
  * @param created Time when this session was opened
  * @param deprecatedAfter Time when this session was closed
  * @author Mikko Hilpinen
  * @since 01.05.2023, v2.0
  */
case class SessionData(userId: Int, created: Instant = Now, deprecatedAfter: Option[Instant] = None) 
	extends ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this session has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	
	/**
	  * Whether this session is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("userId" -> userId, "created" -> created, "deprecatedAfter" -> deprecatedAfter))
}

