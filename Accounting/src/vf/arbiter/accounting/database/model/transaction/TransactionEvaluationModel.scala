package vf.arbiter.accounting.database.model.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import utopia.vault.nosql.storable.deprecation.DeprecatableAfter
import vf.arbiter.accounting.database.factory.transaction.TransactionEvaluationFactory
import vf.arbiter.accounting.model.partial.transaction.TransactionEvaluationData
import vf.arbiter.accounting.model.stored.transaction.TransactionEvaluation

import java.time.Instant

/**
  * Used for constructing TransactionEvaluationModel instances and for inserting transaction evaluations to
  *  the database
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object TransactionEvaluationModel 
	extends DataInserter[TransactionEvaluationModel, TransactionEvaluation, TransactionEvaluationData] 
		with DeprecatableAfter[TransactionEvaluationModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains transaction evaluation transaction id
	  */
	val transactionIdAttName = "transactionId"
	
	/**
	  * Name of the property that contains transaction evaluation type id
	  */
	val typeIdAttName = "typeId"
	
	/**
	  * Name of the property that contains transaction evaluation vat ratio
	  */
	val vatRatioAttName = "vatRatio"
	
	/**
	  * Name of the property that contains transaction evaluation other party alias
	  */
	val otherPartyAliasAttName = "otherPartyAlias"
	
	/**
	  * Name of the property that contains transaction evaluation creator id
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains transaction evaluation created
	  */
	val createdAttName = "created"
	
	/**
	  * Name of the property that contains transaction evaluation deprecated after
	  */
	val deprecatedAfterAttName = "deprecatedAfter"
	
	/**
	  * Name of the property that contains transaction evaluation manual
	  */
	val manualAttName = "manual"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains transaction evaluation transaction id
	  */
	def transactionIdColumn = table(transactionIdAttName)
	
	/**
	  * Column that contains transaction evaluation type id
	  */
	def typeIdColumn = table(typeIdAttName)
	
	/**
	  * Column that contains transaction evaluation vat ratio
	  */
	def vatRatioColumn = table(vatRatioAttName)
	
	/**
	  * Column that contains transaction evaluation other party alias
	  */
	def otherPartyAliasColumn = table(otherPartyAliasAttName)
	
	/**
	  * Column that contains transaction evaluation creator id
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains transaction evaluation created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains transaction evaluation deprecated after
	  */
	def deprecatedAfterColumn = table(deprecatedAfterAttName)
	
	/**
	  * Column that contains transaction evaluation manual
	  */
	def manualColumn = table(manualAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = TransactionEvaluationFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: TransactionEvaluationData) = 
		apply(None, Some(data.transactionId), Some(data.typeId), Some(data.vatRatio), data.otherPartyAlias, 
			data.creatorId, Some(data.created), data.deprecatedAfter, Some(data.manual))
	
	override protected def complete(id: Value, data: TransactionEvaluationData) = 
		TransactionEvaluation(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this transaction evaluation was added to the database
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who added this evaluation. None if unknown or if not applicable.
	  * @return A model containing only the specified creator id
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param deprecatedAfter Time when this evaluation was replaced or cancelled. None while valid.
	  * @return A model containing only the specified deprecated after
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = Some(deprecatedAfter))
	
	/**
	  * @param id A transaction evaluation id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * 
		@param manual Whether this evaluation is manually performed by a human. False if performed by an algorithm.
	  * @return A model containing only the specified manual
	  */
	def withManual(manual: Boolean) = apply(manual = Some(manual))
	
	/**
	  * @param otherPartyAlias An alias given to the other party of this transaction. Empty if no alias
	  *  has been specified.
	  * @return A model containing only the specified other party alias
	  */
	def withOtherPartyAlias(otherPartyAlias: String) = apply(otherPartyAlias = otherPartyAlias)
	
	/**
	  * @param transactionId Id of the described transaction.
	  * @return A model containing only the specified transaction id
	  */
	def withTransactionId(transactionId: Int) = apply(transactionId = Some(transactionId))
	
	/**
	  * @param typeId Id of the assigned type of this transaction
	  * @return A model containing only the specified type id
	  */
	def withTypeId(typeId: Int) = apply(typeId = Some(typeId))
	
	/**
	  * @param vatRatio Ratio of VAT applied to this transaction, where 0.5 is 50% and 1.0 is 100%.
	  * @return A model containing only the specified vat ratio
	  */
	def withVatRatio(vatRatio: Double) = apply(vatRatio = Some(vatRatio))
}

/**
  * Used for interacting with TransactionEvaluations in the database
  * @param id transaction evaluation database id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class TransactionEvaluationModel(id: Option[Int] = None, transactionId: Option[Int] = None, 
	typeId: Option[Int] = None, vatRatio: Option[Double] = None, otherPartyAlias: String = "", 
	creatorId: Option[Int] = None, created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None, 
	manual: Option[Boolean] = None) 
	extends StorableWithFactory[TransactionEvaluation]
{
	// IMPLEMENTED	--------------------
	
	override def factory = TransactionEvaluationModel.factory
	
	override def valueProperties = {
		import TransactionEvaluationModel._
		Vector("id" -> id, transactionIdAttName -> transactionId, typeIdAttName -> typeId, 
			vatRatioAttName -> vatRatio, otherPartyAliasAttName -> otherPartyAlias, 
			creatorIdAttName -> creatorId, createdAttName -> created, 
			deprecatedAfterAttName -> deprecatedAfter, manualAttName -> manual)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this transaction evaluation was added to the database
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who added this evaluation. None if unknown or if not applicable.
	  * @return A new copy of this model with the specified creator id
	  */
	def withCreatorId(creatorId: Int) = copy(creatorId = Some(creatorId))
	
	/**
	  * @param deprecatedAfter Time when this evaluation was replaced or cancelled. None while valid.
	  * @return A new copy of this model with the specified deprecated after
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
	
	/**
	  * 
		@param manual Whether this evaluation is manually performed by a human. False if performed by an algorithm.
	  * @return A new copy of this model with the specified manual
	  */
	def withManual(manual: Boolean) = copy(manual = Some(manual))
	
	/**
	  * @param otherPartyAlias An alias given to the other party of this transaction. Empty if no alias
	  *  has been specified.
	  * @return A new copy of this model with the specified other party alias
	  */
	def withOtherPartyAlias(otherPartyAlias: String) = copy(otherPartyAlias = otherPartyAlias)
	
	/**
	  * @param transactionId Id of the described transaction.
	  * @return A new copy of this model with the specified transaction id
	  */
	def withTransactionId(transactionId: Int) = copy(transactionId = Some(transactionId))
	
	/**
	  * @param typeId Id of the assigned type of this transaction
	  * @return A new copy of this model with the specified type id
	  */
	def withTypeId(typeId: Int) = copy(typeId = Some(typeId))
	
	/**
	  * @param vatRatio Ratio of VAT applied to this transaction, where 0.5 is 50% and 1.0 is 100%.
	  * @return A new copy of this model with the specified vat ratio
	  */
	def withVatRatio(vatRatio: Double) = copy(vatRatio = Some(vatRatio))
}

