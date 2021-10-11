package vf.arbiter.core.model.partial.invoice

import java.time.{Instant, LocalDate}
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.{Days, Now}

/**
  * Represents a bill / an invoice sent by one company to another to request a monetary transfer / payment
  * @param senderCompanyId Id of the company who sent this invoice (payment recipient)
  * @param recipientCompanyId Id of the recipient company of this invoice
  * @param referenceCode A custom reference code used by the sender to identify this invoice
  * @param created Time when this Invoice was first created
  * @param creatorId Id of the user who created this invoice
  * @param paymentDurationDays Number of days during which this invoice can be paid before additional consequences
  * @param productDeliveryDate Date when the sold products were delivered, if applicable
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
case class InvoiceData(senderCompanyId: Int, recipientCompanyId: Int, referenceCode: String, 
	created: Instant = Now, creatorId: Int, paymentDurationDays: Days = Days(30), 
	productDeliveryDate: Option[LocalDate] = None) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("sender_company_id" -> senderCompanyId, "recipient_company_id" -> recipientCompanyId, 
			"reference_code" -> referenceCode, "created" -> created, "creator_id" -> creatorId, 
			"payment_duration_days" -> paymentDurationDays.length, 
			"product_delivery_date" -> productDeliveryDate))
}

