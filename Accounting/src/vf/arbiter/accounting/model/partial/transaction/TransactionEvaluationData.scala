package vf.arbiter.accounting.model.partial.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.BooleanType
import utopia.flow.generic.model.mutable.DataType.DoubleType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object TransactionEvaluationData extends FromModelFactoryWithSchema[TransactionEvaluationData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("transactionId", IntType, Vector("transaction_id")), 
			PropertyDeclaration("typeId", IntType, Vector("type_id")), PropertyDeclaration("vatRatio", 
			DoubleType, Vector("vat_ratio"), 0.0), PropertyDeclaration("otherPartyAlias", StringType, 
			Vector("other_party_alias"), isOptional = true), PropertyDeclaration("creatorId", IntType, 
			Vector("creator_id"), isOptional = true), PropertyDeclaration("created", InstantType, 
			isOptional = true), PropertyDeclaration("deprecatedAfter", InstantType, 
			Vector("deprecated_after"), isOptional = true), PropertyDeclaration("manual", BooleanType, 
			Vector(), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		TransactionEvaluationData(valid("transactionId").getInt, valid("typeId").getInt, 
			valid("vatRatio").getDouble, valid("otherPartyAlias").getString, valid("creatorId").int, 
			valid("created").getInstant, valid("deprecatedAfter").instant, valid("manual").getBoolean)
}

/**
  * Lists information that's added on top of a raw transaction record. Typically based on user input.
  * @param transactionId Id of the described transaction.
  * @param typeId Id of the assigned type of this transaction
  * @param vatRatio Ratio of VAT applied to this transaction, where 0.5 is 50% and 1.0 is 100%.
  * @param otherPartyAlias An alias given to the other party of this transaction. Empty if no alias
  *  has been specified.
  * @param creatorId Id of the user who added this evaluation. None if unknown or if not applicable.
  * @param created Time when this transaction evaluation was added to the database
  * @param deprecatedAfter Time when this evaluation was replaced or cancelled. None while valid.
  * 
	@param manual Whether this evaluation is manually performed by a human. False if performed by an algorithm.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class TransactionEvaluationData(transactionId: Int, typeId: Int, vatRatio: Double = 0.0, 
	otherPartyAlias: String = "", creatorId: Option[Int] = None, created: Instant = Now, 
	deprecatedAfter: Option[Instant] = None, manual: Boolean = false) 
	extends ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this transaction evaluation has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	
	/**
	  * Whether this transaction evaluation is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("transactionId" -> transactionId, "typeId" -> typeId, "vatRatio" -> vatRatio, 
			"otherPartyAlias" -> otherPartyAlias, "creatorId" -> creatorId, "created" -> created, 
			"deprecatedAfter" -> deprecatedAfter, "manual" -> manual))
}

