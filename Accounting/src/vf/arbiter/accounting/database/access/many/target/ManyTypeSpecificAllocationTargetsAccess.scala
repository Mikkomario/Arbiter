package vf.arbiter.accounting.database.access.many.target

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.target.TypeSpecificAllocationTargetFactory
import vf.arbiter.accounting.database.model.target.TypeSpecificAllocationTargetModel
import vf.arbiter.accounting.model.stored.target.TypeSpecificAllocationTarget

object ManyTypeSpecificAllocationTargetsAccess
{
	// NESTED	--------------------
	
	private class ManyTypeSpecificAllocationTargetsSubView(condition: Condition) 
		extends ManyTypeSpecificAllocationTargetsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple type specific allocation targets at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait ManyTypeSpecificAllocationTargetsAccess 
	extends ManyRowModelAccess[TypeSpecificAllocationTarget] 
		with FilterableView[ManyTypeSpecificAllocationTargetsAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * parent ids of the accessible type specific allocation targets
	  */
	def parentIds(implicit connection: Connection) = pullColumn(model.parentIdColumn).map { v => v.getInt }
	
	/**
	  * type ids of the accessible type specific allocation targets
	  */
	def typeIds(implicit connection: Connection) = pullColumn(model.typeIdColumn).map { v => v.getInt }
	
	/**
	  * ratios of the accessible type specific allocation targets
	  */
	def ratios(implicit connection: Connection) = pullColumn(model.ratioColumn).map { v => v.getDouble }
	
	/**
	  * are maximums of the accessible type specific allocation targets
	  */
	def areMaximums(implicit connection: Connection) = pullColumn(model.isMaximumColumn)
		.map { v => v.getBoolean }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = TypeSpecificAllocationTargetModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TypeSpecificAllocationTargetFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyTypeSpecificAllocationTargetsAccess = 
		new ManyTypeSpecificAllocationTargetsAccess
			.ManyTypeSpecificAllocationTargetsSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the are maximums of the targeted type specific allocation targets
	  * @param newIsMaximum A new is maximum to assign
	  * @return Whether any type specific allocation target was affected
	  */
	def areMaximums_=(newIsMaximum: Boolean)(implicit connection: Connection) = 
		putColumn(model.isMaximumColumn, newIsMaximum)
	
	/**
	  * Updates the parent ids of the targeted type specific allocation targets
	  * @param newParentId A new parent id to assign
	  * @return Whether any type specific allocation target was affected
	  */
	def parentIds_=(newParentId: Int)(implicit connection: Connection) = 
		putColumn(model.parentIdColumn, newParentId)
	
	/**
	  * Updates the ratios of the targeted type specific allocation targets
	  * @param newRatio A new ratio to assign
	  * @return Whether any type specific allocation target was affected
	  */
	def ratios_=(newRatio: Double)(implicit connection: Connection) = putColumn(model.ratioColumn, newRatio)
	
	/**
	  * Updates the type ids of the targeted type specific allocation targets
	  * @param newTypeId A new type id to assign
	  * @return Whether any type specific allocation target was affected
	  */
	def typeIds_=(newTypeId: Int)(implicit connection: Connection) = putColumn(model.typeIdColumn, newTypeId)
}

