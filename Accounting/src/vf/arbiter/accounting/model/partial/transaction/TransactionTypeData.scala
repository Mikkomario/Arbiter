package vf.arbiter.accounting.model.partial.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.BooleanType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object TransactionTypeData extends FromModelFactoryWithSchema[TransactionTypeData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("parentId", IntType, Vector("parent_id"), 
			isOptional = true), PropertyDeclaration("creatorId", IntType, Vector("creator_id"), 
			isOptional = true), PropertyDeclaration("created", InstantType, isOptional = true), 
			PropertyDeclaration("preApplied", BooleanType, Vector("pre_applied"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		TransactionTypeData(valid("parentId").int, valid("creatorId").int, valid("created").getInstant, 
			valid("preApplied").getBoolean)
}

/**
  * Represents a category or a type of transaction
  * @param parentId Id of the parent type of this type. None if this is a root/main category.
  * @param creatorId Reference to the user that created this transaction type
  * @param created Time when this transaction type was added to the database
  * @param preApplied Whether these transaction types should be immediately as income or expense, 
  * before targets are applied. 
  * E.g. some expenses may be deducted from income instead of considered additional spending. 
  * Main input sources should also be pre-applied.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class TransactionTypeData(parentId: Option[Int] = None, creatorId: Option[Int] = None, 
	created: Instant = Now, preApplied: Boolean = false) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("parentId" -> parentId, "creatorId" -> creatorId, "created" -> created, 
			"preApplied" -> preApplied))
}

