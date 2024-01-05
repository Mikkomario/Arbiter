package vf.arbiter.accounting.database.access.many.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.EvaluatedTransactionFactory
import vf.arbiter.accounting.database.model.transaction.TransactionEvaluationModel
import vf.arbiter.accounting.model.combined.transaction.EvaluatedTransaction

import java.time.Instant

object ManyEvaluatedTransactionsAccess
{
	// NESTED	--------------------
	
	private class SubAccess(condition: Condition) extends ManyEvaluatedTransactionsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return multiple evaluated transactions at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024
  */
trait ManyEvaluatedTransactionsAccess 
	extends ManyTransactionsAccessLike[EvaluatedTransaction, ManyEvaluatedTransactionsAccess] 
		with ManyRowModelAccess[EvaluatedTransaction]
{
	// COMPUTED	--------------------
	
	/**
	  * transaction ids of the accessible transaction evaluations
	  */
	def evaluationTransactionIds(implicit connection: Connection) = 
		pullColumn(evaluationModel.transactionIdColumn).map { v => v.getInt }
	
	/**
	  * type ids of the accessible transaction evaluations
	  */
	def evaluationTypeIds(implicit connection: Connection) = 
		pullColumn(evaluationModel.typeIdColumn).map { v => v.getInt }
	
	/**
	  * vat ratios of the accessible transaction evaluations
	  */
	def evaluationVatRatios(implicit connection: Connection) = 
		pullColumn(evaluationModel.vatRatioColumn).map { v => v.getDouble }
	
	/**
	  * other party aliases of the accessible transaction evaluations
	  */
	def evaluationOtherPartyAliases(implicit connection: Connection) = 
		pullColumn(evaluationModel.otherPartyAliasColumn).flatMap { _.string }
	
	/**
	  * creator ids of the accessible transaction evaluations
	  */
	def evaluationCreatorIds(implicit connection: Connection) = 
		pullColumn(evaluationModel.creatorIdColumn).flatMap { v => v.int }
	
	/**
	  * creation times of the accessible transaction evaluations
	  */
	def evaluationCreationTimes(implicit connection: Connection) = 
		pullColumn(evaluationModel.createdColumn).map { v => v.getInstant }
	
	/**
	  * deprecation times of the accessible transaction evaluations
	  */
	def evaluationDeprecationTimes(implicit connection: Connection) = 
		pullColumn(evaluationModel.deprecatedAfterColumn).flatMap { v => v.instant }
	
	/**
	  * are manual of the accessible transaction evaluations
	  */
	def evaluationAreManual(implicit connection: Connection) = 
		pullColumn(evaluationModel.manualColumn).map { v => v.getBoolean }
	
	/**
	  * Model (factory) used for interacting the transaction evaluations associated 
		with this evaluated transaction
	  */
	protected def evaluationModel = TransactionEvaluationModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = EvaluatedTransactionFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyEvaluatedTransactionsAccess = 
		new ManyEvaluatedTransactionsAccess.SubAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the are manual of the targeted transaction evaluations
	  * @param newManual A new manual to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationAreManual_=(newManual: Boolean)(implicit connection: Connection) = 
		putColumn(evaluationModel.manualColumn, newManual)
	
	/**
	  * Updates the creation times of the targeted transaction evaluations
	  * @param newCreated A new created to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationCreationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(evaluationModel.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted transaction evaluations
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationCreatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(evaluationModel.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the deprecation times of the targeted transaction evaluations
	  * @param newDeprecatedAfter A new deprecated after to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationDeprecationTimes_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(evaluationModel.deprecatedAfterColumn, newDeprecatedAfter)
	
	/**
	  * Updates the other party aliases of the targeted transaction evaluations
	  * @param newOtherPartyAlias A new other party alias to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationOtherPartyAliases_=(newOtherPartyAlias: String)(implicit connection: Connection) = 
		putColumn(evaluationModel.otherPartyAliasColumn, newOtherPartyAlias)
	
	/**
	  * Updates the transaction ids of the targeted transaction evaluations
	  * @param newTransactionId A new transaction id to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationTransactionIds_=(newTransactionId: Int)(implicit connection: Connection) = 
		putColumn(evaluationModel.transactionIdColumn, newTransactionId)
	
	/**
	  * Updates the type ids of the targeted transaction evaluations
	  * @param newTypeId A new type id to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationTypeIds_=(newTypeId: Int)(implicit connection: Connection) = 
		putColumn(evaluationModel.typeIdColumn, newTypeId)
	
	/**
	  * Updates the vat ratios of the targeted transaction evaluations
	  * @param newVatRatio A new vat ratio to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def evaluationVatRatios_=(newVatRatio: Double)(implicit connection: Connection) = 
		putColumn(evaluationModel.vatRatioColumn, newVatRatio)
}

