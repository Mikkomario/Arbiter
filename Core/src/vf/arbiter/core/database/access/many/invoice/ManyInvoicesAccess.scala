package vf.arbiter.core.database.access.many.invoice

import java.time.{Instant, LocalDate}
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Days
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.invoice.InvoiceFactory
import vf.arbiter.core.database.model.invoice.InvoiceModel
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * A common trait for access points which target multiple Invoices at a time
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait ManyInvoicesAccess extends ManyRowModelAccess[Invoice] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * senderCompanyIds of the accessible Invoices
	  */
	def senderCompanyIds(implicit connection: Connection) = 
		pullColumn(model.senderCompanyIdColumn).flatMap { value => value.int }
	
	/**
	  * recipientCompanyIds of the accessible Invoices
	  */
	def recipientCompanyIds(implicit connection: Connection) = 
		pullColumn(model.recipientCompanyIdColumn).flatMap { value => value.int }
	
	/**
	  * referenceCodes of the accessible Invoices
	  */
	def referenceCodes(implicit connection: Connection) = 
		pullColumn(model.referenceCodeColumn).flatMap { value => value.string }
	
	/**
	  * createds of the accessible Invoices
	  */
	def createds(implicit connection: Connection) = 
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	
	/**
	  * paymentDurationDayss of the accessible Invoices
	  */
	def paymentDurationDayss(implicit connection: Connection) = 
		pullColumn(model.paymentDurationDaysColumn).flatMap { value => value.int.map { Days(_) } }
	
	/**
	  * productDeliveryDates of the accessible Invoices
	  */
	def productDeliveryDates(implicit connection: Connection) = 
		pullColumn(model.productDeliveryDateColumn).flatMap { value => value.localDate }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoiceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceFactory
	
	override protected def defaultOrdering = Some(factory.defaultOrdering)
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the created of the targeted Invoice instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any Invoice instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the paymentDurationDays of the targeted Invoice instance(s)
	  * @param newPaymentDurationDays A new paymentDurationDays to assign
	  * @return Whether any Invoice instance was affected
	  */
	def paymentDurationDays_=(newPaymentDurationDays: Days)(implicit connection: Connection) = 
		putColumn(model.paymentDurationDaysColumn, newPaymentDurationDays.length)
	
	/**
	  * Updates the productDeliveryDate of the targeted Invoice instance(s)
	  * @param newProductDeliveryDate A new productDeliveryDate to assign
	  * @return Whether any Invoice instance was affected
	  */
	def productDeliveryDate_=(newProductDeliveryDate: LocalDate)(implicit connection: Connection) = 
		putColumn(model.productDeliveryDateColumn, newProductDeliveryDate)
	
	/**
	  * Updates the recipientCompanyId of the targeted Invoice instance(s)
	  * @param newRecipientCompanyId A new recipientCompanyId to assign
	  * @return Whether any Invoice instance was affected
	  */
	def recipientCompanyId_=(newRecipientCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.recipientCompanyIdColumn, newRecipientCompanyId)
	
	/**
	  * Updates the referenceCode of the targeted Invoice instance(s)
	  * @param newReferenceCode A new referenceCode to assign
	  * @return Whether any Invoice instance was affected
	  */
	def referenceCode_=(newReferenceCode: String)(implicit connection: Connection) = 
		putColumn(model.referenceCodeColumn, newReferenceCode)
	
	/**
	  * Updates the senderCompanyId of the targeted Invoice instance(s)
	  * @param newSenderCompanyId A new senderCompanyId to assign
	  * @return Whether any Invoice instance was affected
	  */
	def senderCompanyId_=(newSenderCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.senderCompanyIdColumn, newSenderCompanyId)
}
