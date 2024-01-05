package vf.arbiter.accounting.database.access.single.transaction.evaluation

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NullDeprecatableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.TransactionEvaluationFactory
import vf.arbiter.accounting.database.model.transaction.TransactionEvaluationModel
import vf.arbiter.accounting.model.stored.transaction.TransactionEvaluation

import java.time.Instant

object UniqueTransactionEvaluationAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueTransactionEvaluationAccess = 
		new _UniqueTransactionEvaluationAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueTransactionEvaluationAccess(condition: Condition) 
		extends UniqueTransactionEvaluationAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct transaction evaluations.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait UniqueTransactionEvaluationAccess 
	extends SingleRowModelAccess[TransactionEvaluation] 
		with NullDeprecatableView[UniqueTransactionEvaluationAccess] 
		with DistinctModelAccess[TransactionEvaluation, Option[TransactionEvaluation], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the described transaction.. None if no transaction evaluation (or value) was found.
	  */
	def transactionId(implicit connection: Connection) = pullColumn(model.transactionIdColumn).int
	
	/**
	  * Id of the assigned type of this transaction. None if no transaction evaluation (or value) was found.
	  */
	def typeId(implicit connection: Connection) = pullColumn(model.typeIdColumn).int
	
	/**
	  * Ratio of VAT applied to this transaction, 
	  * where 0.5 is 50% and 1.0 is 100%.. None if no transaction evaluation (or value) was found.
	  */
	def vatRatio(implicit connection: Connection) = pullColumn(model.vatRatioColumn).double
	
	/**
	  * An alias given to the other party of this transaction. Empty if no alias has been specified.. None if
	  *  no transaction evaluation (or value) was found.
	  */
	def otherPartyAlias(implicit connection: Connection) = pullColumn(model.otherPartyAliasColumn).getString
	
	/**
	  * Id of the user who added this evaluation. None if unknown or if not applicable.. None if
	  *  no transaction evaluation (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * 
		Time when this transaction evaluation was added to the database. None if no transaction evaluation (or value)
	  *  was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Time when this evaluation was replaced or cancelled. None while valid.. None if
	  *  no transaction evaluation (or value) was found.
	  */
	def deprecatedAfter(implicit connection: Connection) = pullColumn(model.deprecatedAfterColumn).instant
	
	/**
	  * Whether this evaluation is manually performed by a human. False if performed by an algorithm.. None if
	  *  no transaction evaluation (or value) was found.
	  */
	def manual(implicit connection: Connection) = pullColumn(model.manualColumn).boolean
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = TransactionEvaluationModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TransactionEvaluationFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueTransactionEvaluationAccess = 
		new UniqueTransactionEvaluationAccess._UniqueTransactionEvaluationAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted transaction evaluations
	  * @param newCreated A new created to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted transaction evaluations
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the deprecation times of the targeted transaction evaluations
	  * @param newDeprecatedAfter A new deprecated after to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def deprecatedAfter_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.deprecatedAfterColumn, newDeprecatedAfter)
	
	/**
	  * Updates the are manual of the targeted transaction evaluations
	  * @param newManual A new manual to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def manual_=(newManual: Boolean)(implicit connection: Connection) = putColumn(model.manualColumn, 
		newManual)
	
	/**
	  * Updates the other party aliases of the targeted transaction evaluations
	  * @param newOtherPartyAlias A new other party alias to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def otherPartyAlias_=(newOtherPartyAlias: String)(implicit connection: Connection) = 
		putColumn(model.otherPartyAliasColumn, newOtherPartyAlias)
	
	/**
	  * Updates the transaction ids of the targeted transaction evaluations
	  * @param newTransactionId A new transaction id to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def transactionId_=(newTransactionId: Int)(implicit connection: Connection) = 
		putColumn(model.transactionIdColumn, newTransactionId)
	
	/**
	  * Updates the type ids of the targeted transaction evaluations
	  * @param newTypeId A new type id to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def typeId_=(newTypeId: Int)(implicit connection: Connection) = putColumn(model.typeIdColumn, newTypeId)
	
	/**
	  * Updates the vat ratios of the targeted transaction evaluations
	  * @param newVatRatio A new vat ratio to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def vatRatio_=(newVatRatio: Double)(implicit connection: Connection) = 
		putColumn(model.vatRatioColumn, newVatRatio)
}

