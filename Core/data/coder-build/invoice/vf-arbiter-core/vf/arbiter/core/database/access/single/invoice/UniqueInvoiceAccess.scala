package vf.arbiter.core.database.access.single.invoice

import java.time.{Instant, LocalDate}
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Days
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.invoice.InvoiceFactory
import vf.arbiter.core.database.model.invoice.InvoiceModel
import vf.arbiter.core.model.stored.invoice.Invoice

/**
  * A common trait for access points that return individual and distinct Invoices.
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
trait UniqueInvoiceAccess 
	extends SingleRowModelAccess[Invoice] with DistinctModelAccess[Invoice, Option[Invoice], Value] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the company who sent this invoice (payment recipient). None if no instance (or value) was found.
	  */
	def senderCompanyId(implicit connection: Connection) = pullColumn(model.senderCompanyIdColumn).int
	
	/**
	  * Id of the recipient company of this invoice. None if no instance (or value) was found.
	  */
	def recipientCompanyId(implicit connection: Connection) = pullColumn(model.recipientCompanyIdColumn).int
	
	/**
	  * A custom reference code used by the sender to identify this invoice. None if no instance (or value) was found.
	  */
	def referenceCode(implicit connection: Connection) = pullColumn(model.referenceCodeColumn).string
	
	/**
	  * Time when this Invoice was first created. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Id of the user who created this invoice. None if no instance (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Number of days during which this invoice can be paid before additional consequences. None if no instance (or value) was found.
	  */
	def paymentDurationDays(implicit connection: Connection) = 
		pullColumn(model.paymentDurationDaysColumn).int.map { Days(_) }
	
	/**
	  * Date when the sold products were delivered, if applicable. None if no instance (or value) was found.
	  */
	def productDeliveryDate(implicit connection: Connection) = 
		pullColumn(model.productDeliveryDateColumn).localDate
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = InvoiceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = InvoiceFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the created of the targeted Invoice instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any Invoice instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted Invoice instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any Invoice instance was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
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

