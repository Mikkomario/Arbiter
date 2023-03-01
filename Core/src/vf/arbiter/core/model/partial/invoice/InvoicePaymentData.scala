package vf.arbiter.core.model.partial.invoice

import java.time.{Instant, LocalDate}
import utopia.flow.generic.model.immutable.Model
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.time.{Now, Today}

/**
  * Represents a payment event concerning an invoice you have sent
  * @param invoiceId Id of the invoice that was paid
  * @param date Date when this payment was received
  * @param receivedAmount Received amount in €, including taxes
  * @param remarks Free remarks concerning this payment
  * @param created Time when this payment was registered
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class InvoicePaymentData(invoiceId: Int, date: LocalDate = Today, receivedAmount: Double, 
	remarks: Option[String] = None, created: Instant = Now) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("invoice_id" -> invoiceId, "date" -> date, "received_amount" -> receivedAmount, 
			"remarks" -> remarks, "created" -> created))
}

