package vf.arbiter.accounting.model.partial.account

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.DoubleType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object AccountBalanceData extends FromModelFactoryWithSchema[AccountBalanceData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("accountId", IntType, Vector("account_id")), 
			PropertyDeclaration("balance", DoubleType), PropertyDeclaration("creatorId", IntType, 
			Vector("creator_id"), isOptional = true), PropertyDeclaration("created", InstantType, 
			isOptional = true), PropertyDeclaration("deprecatedAfter", InstantType, 
			Vector("deprecated_after"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		AccountBalanceData(valid("accountId").getInt, valid("balance").getDouble, valid("creatorId").int, 
			valid("created").getInstant, valid("deprecatedAfter").instant)
}

/**
  * Represents a balance (monetary amount) on a bank account at a specific time
  * @param accountId Id of the described bank account
  * @param balance The amount of € on this account in this instance
  * @param creatorId Id of the user who provided this information. None if not known or if not applicable.
  * @param created Time when this value was specified. Also represents the time when this value was accurate.
  * @param deprecatedAfter Time when this statement was cancelled. None while valid.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class AccountBalanceData(accountId: Int, balance: Double, creatorId: Option[Int] = None, 
	created: Instant = Now, deprecatedAfter: Option[Instant] = None) 
	extends ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this account balance has already been deprecated
	  */
	def isDeprecated = deprecatedAfter.isDefined
	
	/**
	  * Whether this account balance is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("accountId" -> accountId, "balance" -> balance, "creatorId" -> creatorId, 
			"created" -> created, "deprecatedAfter" -> deprecatedAfter))
}

