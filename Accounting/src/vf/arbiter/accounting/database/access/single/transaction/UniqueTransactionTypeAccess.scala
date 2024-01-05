package vf.arbiter.accounting.database.access.single.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.TransactionTypeFactory
import vf.arbiter.accounting.database.model.transaction.TransactionTypeModel
import vf.arbiter.accounting.model.stored.transaction.TransactionType

import java.time.Instant

object UniqueTransactionTypeAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueTransactionTypeAccess = new _UniqueTransactionTypeAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueTransactionTypeAccess(condition: Condition) extends UniqueTransactionTypeAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct transaction types.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait UniqueTransactionTypeAccess 
	extends SingleRowModelAccess[TransactionType] with FilterableView[UniqueTransactionTypeAccess] 
		with DistinctModelAccess[TransactionType, Option[TransactionType], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the parent type of this type. None if this is a root/main category.. None if
	  *  no transaction type (or value) was found.
	  */
	def parentId(implicit connection: Connection) = pullColumn(model.parentIdColumn).int
	
	/**
	  * 
		Reference to the user that created this transaction type. None if no transaction type (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * 
		Time when this transaction type was added to the database. None if no transaction type (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Whether these transaction types should be immediately as income or expense, 
		before targets are applied. 
	  * E.g. some expenses may be deducted from income instead of considered additional spending. 
	  * Main input sources should also be pre-applied.. None if no transaction type (or value) was found.
	  */
	def preApplied(implicit connection: Connection) = pullColumn(model.preAppliedColumn).boolean
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = TransactionTypeModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TransactionTypeFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueTransactionTypeAccess = 
		new UniqueTransactionTypeAccess._UniqueTransactionTypeAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted transaction types
	  * @param newCreated A new created to assign
	  * @return Whether any transaction type was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted transaction types
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any transaction type was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the parent ids of the targeted transaction types
	  * @param newParentId A new parent id to assign
	  * @return Whether any transaction type was affected
	  */
	def parentId_=(newParentId: Int)(implicit connection: Connection) = 
		putColumn(model.parentIdColumn, newParentId)
	
	/**
	  * Updates the pre applied of the targeted transaction types
	  * @param newPreApplied A new pre applied to assign
	  * @return Whether any transaction type was affected
	  */
	def preApplied_=(newPreApplied: Boolean)(implicit connection: Connection) = 
		putColumn(model.preAppliedColumn, newPreApplied)
}

