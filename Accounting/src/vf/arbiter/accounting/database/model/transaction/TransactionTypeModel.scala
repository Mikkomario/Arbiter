package vf.arbiter.accounting.database.model.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.accounting.database.factory.transaction.TransactionTypeFactory
import vf.arbiter.accounting.model.partial.transaction.TransactionTypeData
import vf.arbiter.accounting.model.stored.transaction.TransactionType

import java.time.Instant

/**
  * Used for constructing TransactionTypeModel instances and for inserting transaction types to the database
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object TransactionTypeModel extends DataInserter[TransactionTypeModel, TransactionType, TransactionTypeData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains transaction type parent id
	  */
	val parentIdAttName = "parentId"
	
	/**
	  * Name of the property that contains transaction type creator id
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains transaction type created
	  */
	val createdAttName = "created"
	
	/**
	  * Name of the property that contains transaction type pre applied
	  */
	val preAppliedAttName = "preApplied"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains transaction type parent id
	  */
	def parentIdColumn = table(parentIdAttName)
	
	/**
	  * Column that contains transaction type creator id
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains transaction type created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains transaction type pre applied
	  */
	def preAppliedColumn = table(preAppliedAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = TransactionTypeFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: TransactionTypeData) = 
		apply(None, data.parentId, data.creatorId, Some(data.created), Some(data.preApplied))
	
	override protected def complete(id: Value, data: TransactionTypeData) = TransactionType(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this transaction type was added to the database
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Reference to the user that created this transaction type
	  * @return A model containing only the specified creator id
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param id A transaction type id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param parentId Id of the parent type of this type. None if this is a root/main category.
	  * @return A model containing only the specified parent id
	  */
	def withParentId(parentId: Int) = apply(parentId = Some(parentId))
	
	/**
	  * @param preApplied Whether these transaction types should be immediately as income or expense, 
	  * before targets are applied. 
	  * E.g. some expenses may be deducted from income instead of considered additional spending. 
	  * Main input sources should also be pre-applied.
	  * @return A model containing only the specified pre applied
	  */
	def withPreApplied(preApplied: Boolean) = apply(preApplied = Some(preApplied))
}

/**
  * Used for interacting with TransactionTypes in the database
  * @param id transaction type database id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class TransactionTypeModel(id: Option[Int] = None, parentId: Option[Int] = None, 
	creatorId: Option[Int] = None, created: Option[Instant] = None, preApplied: Option[Boolean] = None) 
	extends StorableWithFactory[TransactionType]
{
	// IMPLEMENTED	--------------------
	
	override def factory = TransactionTypeModel.factory
	
	override def valueProperties = {
		import TransactionTypeModel._
		Vector("id" -> id, parentIdAttName -> parentId, creatorIdAttName -> creatorId, 
			createdAttName -> created, preAppliedAttName -> preApplied)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this transaction type was added to the database
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param creatorId Reference to the user that created this transaction type
	  * @return A new copy of this model with the specified creator id
	  */
	def withCreatorId(creatorId: Int) = copy(creatorId = Some(creatorId))
	
	/**
	  * @param parentId Id of the parent type of this type. None if this is a root/main category.
	  * @return A new copy of this model with the specified parent id
	  */
	def withParentId(parentId: Int) = copy(parentId = Some(parentId))
	
	/**
	  * @param preApplied Whether these transaction types should be immediately as income or expense, 
	  * before targets are applied. 
	  * E.g. some expenses may be deducted from income instead of considered additional spending. 
	  * Main input sources should also be pre-applied.
	  * @return A new copy of this model with the specified pre applied
	  */
	def withPreApplied(preApplied: Boolean) = copy(preApplied = Some(preApplied))
}

