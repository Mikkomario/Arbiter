package vf.arbiter.accounting.database.access.many.transaction

import utopia.citadel.database.access.many.description.ManyDescribedAccess
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.access.many.description.DbTransactionTypeDescriptions
import vf.arbiter.accounting.database.factory.transaction.TransactionTypeFactory
import vf.arbiter.accounting.database.model.transaction.TransactionTypeModel
import vf.arbiter.accounting.model.combined.transaction.DescribedTransactionType
import vf.arbiter.accounting.model.stored.transaction.TransactionType

import java.time.Instant

object ManyTransactionTypesAccess
{
	// NESTED	--------------------
	
	private class ManyTransactionTypesSubView(condition: Condition) extends ManyTransactionTypesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple transaction types at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait ManyTransactionTypesAccess 
	extends ManyRowModelAccess[TransactionType] with FilterableView[ManyTransactionTypesAccess] 
		with ManyDescribedAccess[TransactionType, DescribedTransactionType]
{
	// COMPUTED	--------------------
	
	/**
	  * parent ids of the accessible transaction types
	  */
	def parentIds(implicit connection: Connection) = pullColumn(model.parentIdColumn).flatMap { v => v.int }
	
	/**
	  * creator ids of the accessible transaction types
	  */
	def creatorIds(implicit connection: Connection) = pullColumn(model.creatorIdColumn).flatMap { v => v.int }
	
	/**
	  * creation times of the accessible transaction types
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	/**
	  * pre applied of the accessible transaction types
	  */
	def preApplied(implicit connection: Connection) = pullColumn(model.preAppliedColumn)
		.map { v => v.getBoolean }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = TransactionTypeModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TransactionTypeFactory
	
	override protected def describedFactory = DescribedTransactionType
	
	override protected def manyDescriptionsAccess = DbTransactionTypeDescriptions
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyTransactionTypesAccess = 
		new ManyTransactionTypesAccess.ManyTransactionTypesSubView(mergeCondition(filterCondition))
	
	override def idOf(item: TransactionType) = item.id
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted transaction types
	  * @param newCreated A new created to assign
	  * @return Whether any transaction type was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted transaction types
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any transaction type was affected
	  */
	def creatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the parent ids of the targeted transaction types
	  * @param newParentId A new parent id to assign
	  * @return Whether any transaction type was affected
	  */
	def parentIds_=(newParentId: Int)(implicit connection: Connection) = 
		putColumn(model.parentIdColumn, newParentId)
	
	/**
	  * Updates the pre applied of the targeted transaction types
	  * @param newPreApplied A new pre applied to assign
	  * @return Whether any transaction type was affected
	  */
	def preApplied_=(newPreApplied: Boolean)(implicit connection: Connection) = 
		putColumn(model.preAppliedColumn, newPreApplied)
}

