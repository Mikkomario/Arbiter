package vf.arbiter.accounting.database.access.single.target

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.target.TypeSpecificAllocationTargetFactory
import vf.arbiter.accounting.database.model.target.TypeSpecificAllocationTargetModel
import vf.arbiter.accounting.model.stored.target.TypeSpecificAllocationTarget

object UniqueTypeSpecificAllocationTargetAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueTypeSpecificAllocationTargetAccess = 
		new _UniqueTypeSpecificAllocationTargetAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueTypeSpecificAllocationTargetAccess(condition: Condition) 
		extends UniqueTypeSpecificAllocationTargetAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct type specific allocation targets.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait UniqueTypeSpecificAllocationTargetAccess 
	extends SingleRowModelAccess[TypeSpecificAllocationTarget] 
		with FilterableView[UniqueTypeSpecificAllocationTargetAccess] 
		with DistinctModelAccess[TypeSpecificAllocationTarget, Option[TypeSpecificAllocationTarget], Value] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * 
		Id of the target to which this specific value belongs. None if no type specific allocation target (or value)
	  *  was found.
	  */
	def parentId(implicit connection: Connection) = pullColumn(model.parentIdColumn).int
	
	/**
	  * Id of the allocated transaction type. None if no type specific allocation target (or value) was found.
	  */
	def typeId(implicit connection: Connection) = pullColumn(model.typeIdColumn).int
	
	/**
	  * 
		The targeted ratio of total after-expenses income that should be allocated into this transaction type. 
	  * If a ratio has been specified for a parent transaction type, 
	  * 
		this ratio is applied to the parent's portion (E.g. ratio of 1.0 would allocate 100% of the parent's share to this child type). 
	  * [0,1]. None if no type specific allocation target (or value) was found.
	  */
	def ratio(implicit connection: Connection) = pullColumn(model.ratioColumn).double
	
	/**
	  * 
		Whether this target represents the largest allowed value. False if this represents the minimum target..
	  *  None if no type specific allocation target (or value) was found.
	  */
	def isMaximum(implicit connection: Connection) = pullColumn(model.isMaximumColumn).boolean
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = TypeSpecificAllocationTargetModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TypeSpecificAllocationTargetFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueTypeSpecificAllocationTargetAccess = 
		new UniqueTypeSpecificAllocationTargetAccess._UniqueTypeSpecificAllocationTargetAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the are maximums of the targeted type specific allocation targets
	  * @param newIsMaximum A new is maximum to assign
	  * @return Whether any type specific allocation target was affected
	  */
	def isMaximum_=(newIsMaximum: Boolean)(implicit connection: Connection) = 
		putColumn(model.isMaximumColumn, newIsMaximum)
	
	/**
	  * Updates the parent ids of the targeted type specific allocation targets
	  * @param newParentId A new parent id to assign
	  * @return Whether any type specific allocation target was affected
	  */
	def parentId_=(newParentId: Int)(implicit connection: Connection) = 
		putColumn(model.parentIdColumn, newParentId)
	
	/**
	  * Updates the ratios of the targeted type specific allocation targets
	  * @param newRatio A new ratio to assign
	  * @return Whether any type specific allocation target was affected
	  */
	def ratio_=(newRatio: Double)(implicit connection: Connection) = putColumn(model.ratioColumn, newRatio)
	
	/**
	  * Updates the type ids of the targeted type specific allocation targets
	  * @param newTypeId A new type id to assign
	  * @return Whether any type specific allocation target was affected
	  */
	def typeId_=(newTypeId: Int)(implicit connection: Connection) = putColumn(model.typeIdColumn, newTypeId)
}

