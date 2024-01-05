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

object InvoicePaymentData extends FromModelFactoryWithSchema[InvoicePaymentData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("invoiceId", IntType, Vector("invoice_id")), 
			PropertyDeclaration("transactionId", IntType, Vector("transaction_id")), 
			PropertyDeclaration("creatorId", IntType, Vector("creator_id"), isOptional = true), 
			PropertyDeclaration("created", InstantType, isOptional = true), PropertyDeclaration("manual", 
			BooleanType, Vector(), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		InvoicePaymentData(valid("invoiceId").getInt, valid("transactionId").getInt, valid("creatorId").int, 
			valid("created").getInstant, valid("manual").getBoolean)
}

/**
  * Links an invoice with the transaction where it was paid
  * @param invoiceId Id of the paid invoice
  * @param transactionId Id of the transaction that paid the linked invoice
  * @param creatorId Id of the user who registered this connection
  * @param created Time when this link was registered
  * @param manual Whether this connection was made manually (true), 
  * or whether it was determined by an automated algorithm (false)
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class InvoicePaymentData(invoiceId: Int, transactionId: Int, creatorId: Option[Int] = None, 
	created: Instant = Now, manual: Boolean = false) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("invoiceId" -> invoiceId, "transactionId" -> transactionId, "creatorId" -> creatorId, 
			"created" -> created, "manual" -> manual))
}

