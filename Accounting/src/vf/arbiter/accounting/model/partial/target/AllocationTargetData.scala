package vf.arbiter.accounting.model.partial.target

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.DoubleType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object AllocationTargetData extends FromModelFactoryWithSchema[AllocationTargetData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("companyId", IntType, Vector("company_id")), 
			PropertyDeclaration("capitalRatio", DoubleType, Vector("capital_ratio")), 
			PropertyDeclaration("appliedSince", InstantType, Vector("applied_since"), isOptional = true), 
			PropertyDeclaration("appliedUntil", InstantType, Vector("applied_until"), isOptional = true), 
			PropertyDeclaration("creatorId", IntType, Vector("creator_id"), isOptional = true), 
			PropertyDeclaration("created", InstantType, isOptional = true), 
			PropertyDeclaration("deprecatedAfter", InstantType, Vector("deprecated_after"), 
			isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		AllocationTargetData(valid("companyId").getInt, valid("capitalRatio").getDouble, 
			valid("appliedSince").getInstant, valid("appliedUntil").instant, valid("creatorId").int, 
			valid("created").getInstant, valid("deprecatedAfter").instant)
}

/**
  * Represents a goal or a target for money-allocation
  * @param companyId Id of the company for which these targets apply
  * @param capitalRatio The targeted (minimum) capital ratio of the income (possibly after certain expenses) [0,
	
  * 1].
  * @param appliedSince The first time from which this target is applied
  * 
	@param appliedUntil Time until which this target was applied. None if applied indefinitely (or until changed).
  * @param creatorId Id of the user who specified these targets
  * @param created Time when this target was specified
  * @param deprecatedAfter Time when this target was cancelled / deprecated. Deprecated targets don't apply, 
  * not even in post.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class AllocationTargetData(companyId: Int, capitalRatio: Double, appliedSince: Instant = Now, 
	appliedUntil: Option[Instant] = None, creatorId: Option[Int] = None, created: Instant = Now, 
	deprecatedAfter: Option[Instant] = None) 
	extends ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this allocation target has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	
	/**
	  * Whether this allocation target is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("companyId" -> companyId, "capitalRatio" -> capitalRatio, 
			"appliedSince" -> appliedSince, "appliedUntil" -> appliedUntil, "creatorId" -> creatorId, 
			"created" -> created, "deprecatedAfter" -> deprecatedAfter))
}

