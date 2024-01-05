package vf.arbiter.accounting.database.access.single.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.NullDeprecatableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.EvaluatedTransactionFactory
import vf.arbiter.accounting.database.model.transaction.TransactionEvaluationModel
import vf.arbiter.accounting.model.combined.transaction.EvaluatedTransaction

import java.time.Instant

object UniqueEvaluatedTransactionAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueEvaluatedTransactionAccess = 
		new _UniqueEvaluatedTransactionAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueEvaluatedTransactionAccess(condition: Condition)
		 extends UniqueEvaluatedTransactionAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return distinct evaluated transactions
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait UniqueEvaluatedTransactionAccess 
	extends UniqueTransactionAccessLike[EvaluatedTransaction] with SingleRowModelAccess[EvaluatedTransaction] 
		with NullDeprecatableView[UniqueEvaluatedTransactionAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the described transaction.. None if no transaction evaluation (or value) was found.
	  */
	def evaluationTransactionId(implicit connection: Connection) = 
		pullColumn(evaluationModel.transactionIdColumn).int
	
	/**
	  * Id of the assigned type of this transaction. None if no transaction evaluation (or value) was found.
	  */
	def evaluationTypeId(implicit connection: Connection) = pullColumn(evaluationModel.typeIdColumn).int
	
	/**
	  * Ratio of VAT applied to this transaction, 
	  * where 0.5 is 50% and 1.0 is 100%.. None if no transaction evaluation (or value) was found.
	  */
	def evaluationVatRatio(implicit connection: Connection) = pullColumn(evaluationModel
		.vatRatioColumn).double
	
	/**
	  * An alias given to the other party of this transaction. Empty if no alias has been specified.. None if
	  *  no transaction evaluation (or value) was found.
	  */
	def evaluationOtherPartyAlias(implicit connection: Connection) = 
		pullColumn(evaluationModel.otherPartyAliasColumn).getString
	
	/**
	  * Id of the user who added this evaluation. None if unknown or if not applicable.. None if
	  *  no transaction evaluation (or value) was found.
	  */
	def evaluationCreatorId(implicit connection: Connection) = pullColumn(evaluationModel.creatorIdColumn).int
	
	/**
	  * 
		Time when this transaction evaluation was added to the database. None if no transaction evaluation (or value)
	  *  was found.
	  */
	def evaluationCreated(implicit connection: Connection) = pullColumn(evaluationModel.createdColumn).instant
	
	/**
	  * Time when this evaluation was replaced or cancelled. None while valid.. None if
	  *  no transaction evaluation (or value) was found.
	  */
	def evaluationDeprecatedAfter(implicit connection: Connection) = 
		pullColumn(evaluationModel.deprecatedAfterColumn).instant
	
	/**
	  * Whether this evaluation is manually performed by a human. False if performed by an algorithm.. None if
	  *  no transaction evaluation (or value) was found.
	  */
	def evaluationManual(implicit connection: Connection) = pullColumn(evaluationModel.manualColumn).boolean
	
	/**
	  * A database model (factory) used for interacting with the linked evaluation
	  */
	protected def evaluationModel = TransactionEvaluationModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = EvaluatedTransactionFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueEvaluatedTransactionAccess = 
		new UniqueEvaluatedTransactionAccess._UniqueEvaluatedTransactionAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted transaction evaluations
	  * @param newCreated A new created to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationCreated_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(evaluationModel.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted transaction evaluations
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationCreatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(evaluationModel.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the deprecation times of the targeted transaction evaluations
	  * @param newDeprecatedAfter A new deprecated after to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationDeprecatedAfter_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(evaluationModel.deprecatedAfterColumn, newDeprecatedAfter)
	
	/**
	  * Updates the are manual of the targeted transaction evaluations
	  * @param newManual A new manual to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationManual_=(newManual: Boolean)(implicit connection: Connection) = 
		putColumn(evaluationModel.manualColumn, newManual)
	
	/**
	  * Updates the other party aliases of the targeted transaction evaluations
	  * @param newOtherPartyAlias A new other party alias to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationOtherPartyAlias_=(newOtherPartyAlias: String)(implicit connection: Connection) = 
		putColumn(evaluationModel.otherPartyAliasColumn, newOtherPartyAlias)
	
	/**
	  * Updates the transaction ids of the targeted transaction evaluations
	  * @param newTransactionId A new transaction id to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationTransactionId_=(newTransactionId: Int)(implicit connection: Connection) = 
		putColumn(evaluationModel.transactionIdColumn, newTransactionId)
	
	/**
	  * Updates the type ids of the targeted transaction evaluations
	  * @param newTypeId A new type id to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationTypeId_=(newTypeId: Int)(implicit connection: Connection) = 
		putColumn(evaluationModel.typeIdColumn, newTypeId)
	
	/**
	  * Updates the vat ratios of the targeted transaction evaluations
	  * @param newVatRatio A new vat ratio to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationVatRatio_=(newVatRatio: Double)(implicit connection: Connection) = 
		putColumn(evaluationModel.vatRatioColumn, newVatRatio)
}

