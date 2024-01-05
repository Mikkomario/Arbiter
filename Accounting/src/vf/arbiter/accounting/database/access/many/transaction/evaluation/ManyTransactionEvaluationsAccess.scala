package vf.arbiter.accounting.database.access.many.transaction.evaluation

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NullDeprecatableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.TransactionEvaluationFactory
import vf.arbiter.accounting.database.model.transaction.TransactionEvaluationModel
import vf.arbiter.accounting.model.stored.transaction.TransactionEvaluation

import java.time.Instant

object ManyTransactionEvaluationsAccess
{
	// NESTED	--------------------
	
	private class ManyTransactionEvaluationsSubView(condition: Condition)
		 extends ManyTransactionEvaluationsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple transaction evaluations at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait ManyTransactionEvaluationsAccess 
	extends ManyRowModelAccess[TransactionEvaluation] 
		with NullDeprecatableView[ManyTransactionEvaluationsAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * transaction ids of the accessible transaction evaluations
	  */
	def transactionIds(implicit connection: Connection) = 
		pullColumn(model.transactionIdColumn).map { v => v.getInt }
	
	/**
	  * type ids of the accessible transaction evaluations
	  */
	def typeIds(implicit connection: Connection) = pullColumn(model.typeIdColumn).map { v => v.getInt }
	
	/**
	  * vat ratios of the accessible transaction evaluations
	  */
	def vatRatios(implicit connection: Connection) = pullColumn(model.vatRatioColumn).map { v => v.getDouble }
	
	/**
	  * other party aliases of the accessible transaction evaluations
	  */
	def otherPartyAliases(implicit connection: Connection) = 
		pullColumn(model.otherPartyAliasColumn).flatMap { _.string }
	
	/**
	  * creator ids of the accessible transaction evaluations
	  */
	def creatorIds(implicit connection: Connection) = pullColumn(model.creatorIdColumn).flatMap { v => v.int }
	
	/**
	  * creation times of the accessible transaction evaluations
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	/**
	  * deprecation times of the accessible transaction evaluations
	  */
	def deprecationTimes(implicit connection: Connection) = 
		pullColumn(model.deprecatedAfterColumn).flatMap { v => v.instant }
	
	/**
	  * are manual of the accessible transaction evaluations
	  */
	def areManual(implicit connection: Connection) = pullColumn(model.manualColumn).map { v => v.getBoolean }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = TransactionEvaluationModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TransactionEvaluationFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyTransactionEvaluationsAccess = 
		new ManyTransactionEvaluationsAccess
			.ManyTransactionEvaluationsSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the are manual of the targeted transaction evaluations
	  * @param newManual A new manual to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def areManual_=(newManual: Boolean)(implicit connection: Connection) = 
		putColumn(model.manualColumn, newManual)
	
	/**
	  * Updates the creation times of the targeted transaction evaluations
	  * @param newCreated A new created to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted transaction evaluations
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def creatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the deprecation times of the targeted transaction evaluations
	  * @param newDeprecatedAfter A new deprecated after to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def deprecationTimes_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.deprecatedAfterColumn, newDeprecatedAfter)
	
	/**
	  * Updates the other party aliases of the targeted transaction evaluations
	  * @param newOtherPartyAlias A new other party alias to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def otherPartyAliases_=(newOtherPartyAlias: String)(implicit connection: Connection) = 
		putColumn(model.otherPartyAliasColumn, newOtherPartyAlias)
	
	/**
	  * Updates the transaction ids of the targeted transaction evaluations
	  * @param newTransactionId A new transaction id to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def transactionIds_=(newTransactionId: Int)(implicit connection: Connection) = 
		putColumn(model.transactionIdColumn, newTransactionId)
	
	/**
	  * Updates the type ids of the targeted transaction evaluations
	  * @param newTypeId A new type id to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def typeIds_=(newTypeId: Int)(implicit connection: Connection) = putColumn(model.typeIdColumn, newTypeId)
	
	/**
	  * Updates the vat ratios of the targeted transaction evaluations
	  * @param newVatRatio A new vat ratio to assign
	  * @return Whether any transaction evaluation was affected
	  */
	def vatRatios_=(newVatRatio: Double)(implicit connection: Connection) = 
		putColumn(model.vatRatioColumn, newVatRatio)
}

