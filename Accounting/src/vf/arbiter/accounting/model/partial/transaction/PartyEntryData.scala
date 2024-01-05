package vf.arbiter.accounting.model.partial.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object PartyEntryData extends FromModelFactoryWithSchema[PartyEntryData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("name", StringType), PropertyDeclaration("created", 
			InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		PartyEntryData(valid("name").getString, valid("created").getInstant)
}

/**
  * Represents a transaction party as described on a bank statement
  * @param name Name of this entity, just as it appeared on a bank statement
  * @param created Time when this party entry was added to the database
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class PartyEntryData(name: String, created: Instant = Now) extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("name" -> name, "created" -> created))
}

