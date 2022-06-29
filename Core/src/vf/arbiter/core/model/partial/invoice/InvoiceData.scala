package vf.arbiter.core.model.partial.invoice

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.TimeExtensions._
import utopia.flow.time.{DateRange, Days, Now}
import vf.arbiter.core.database.access.single.company.{DbCompanyBankAccount, DbCompanyDetails}

import java.time.Instant

/**
  * Represents a bill / an invoice sent by one company to another to request a monetary transfer / payment
  * @param senderCompanyDetailsId Id of the details of the company who sent this invoice (payment recipient)
  * @param recipientCompanyDetailsId Id of the details of the recipient company used in this invoice
  * @param senderBankAccountId Id of the bank account the invoice sender wants the recipient to transfer money to
  * @param languageId Id of the language used in this invoice
  * @param referenceCode A custom reference code used by the sender to identify this invoice
  * @param paymentDuration Number of days during which this invoice can be paid before additional consequences
  * @param productDeliveryDates When were the products delivered for the customer, if applicable
  * @param creatorId Id of the user who created this invoice
  * @param created Time when this invoice was first created
  * @param cancelledAfter Time when this invoice became deprecated. None while this invoice is still valid.
  * @author Mikko Hilpinen
  * @since 31.10.2021, v1.3
  */
case class InvoiceData(senderCompanyDetailsId: Int, recipientCompanyDetailsId: Int, senderBankAccountId: Int,
                       languageId: Int, referenceCode: String, paymentDuration: Days = Days(30),
	                   productDeliveryDates: Option[DateRange] = None, creatorId: Option[Int] = None,
	                   created: Instant = Now, cancelledAfter: Option[Instant] = None)
	extends ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this invoice has already been deprecated
	  */
	def isDeprecated = cancelledAfter.isDefined
	
	/**
	  * Whether this invoice is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	/**
	  * Date when this invoice was created
	  */
	def date = created.toLocalDateTime.toLocalDate
	
	/**
	  * Date when this invoice is due
	  */
	def paymentDeadline = date + paymentDuration
	
	/**
	  * An access point to information concerning invoice sender
	  */
	def senderDetailsAccess = DbCompanyDetails(senderCompanyDetailsId)
	
	/**
	  * An access point to information concerning invoice recipient
	  */
	def recipientDetailsAccess = DbCompanyDetails(recipientCompanyDetailsId)
	
	/**
	  * An access point to the invoice sender's bank account
	  */
	def bankAccountAccess = DbCompanyBankAccount(senderBankAccountId)
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("sender_company_details_id" -> senderCompanyDetailsId, 
			"recipient_company_details_id" -> recipientCompanyDetailsId, 
			"sender_bank_account_id" -> senderBankAccountId, "language_id" -> languageId, 
			"reference_code" -> referenceCode, "payment_duration_days" -> paymentDuration.length, 
			"product_delivery" -> productDeliveryDates.map { _.toModel },
			"creator_id" -> creatorId, "created" -> created, "cancelled_after" -> cancelledAfter))
}

