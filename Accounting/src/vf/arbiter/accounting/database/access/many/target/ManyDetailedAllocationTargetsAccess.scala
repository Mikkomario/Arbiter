package vf.arbiter.accounting.database.access.many.target

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.target.DetailedAllocationTargetFactory
import vf.arbiter.accounting.database.model.target.TypeSpecificAllocationTargetModel
import vf.arbiter.accounting.model.combined.target.DetailedAllocationTarget

object ManyDetailedAllocationTargetsAccess
{
	// NESTED	--------------------
	
	private class SubAccess(condition: Condition) extends ManyDetailedAllocationTargetsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return multiple detailed allocation targets at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024
  */
trait ManyDetailedAllocationTargetsAccess 
	extends ManyAllocationTargetsAccessLike[DetailedAllocationTarget, ManyDetailedAllocationTargetsAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * parent ids of the accessible type specific allocation targets
	  */
	def specificTargetParentIds(implicit connection: Connection) = 
		pullColumn(specificTargetModel.parentIdColumn).map { v => v.getInt }
	
	/**
	  * type ids of the accessible type specific allocation targets
	  */
	def specificTargetTypeIds(implicit connection: Connection) = 
		pullColumn(specificTargetModel.typeIdColumn).map { v => v.getInt }
	
	/**
	  * ratios of the accessible type specific allocation targets
	  */
	def specificTargetRatios(implicit connection: Connection) = 
		pullColumn(specificTargetModel.ratioColumn).map { v => v.getDouble }
	
	/**
	  * are maximums of the accessible type specific allocation targets
	  */
	def specificTargetAreMaximums(implicit connection: Connection) = 
		pullColumn(specificTargetModel.isMaximumColumn).map { v => v.getBoolean }
	
	/**
	  * Model (factory) used for interacting the type specific allocation targets associated 
	  * with this detailed allocation target
	  */
	protected def specificTargetModel = TypeSpecificAllocationTargetModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DetailedAllocationTargetFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyDetailedAllocationTargetsAccess = 
		new ManyDetailedAllocationTargetsAccess.SubAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the are maximums of the targeted type specific allocation targets
	  * @param newIsMaximum A new is maximum to assign
	  * @return Whether any type specific allocation target was affected
	  */
	def specificTargetAreMaximums_=(newIsMaximum: Boolean)(implicit connection: Connection) = 
		putColumn(specificTargetModel.isMaximumColumn, newIsMaximum)
	
	/**
	  * Updates the parent ids of the targeted type specific allocation targets
	  * @param newParentId A new parent id to assign
	  * @return Whether any type specific allocation target was affected
	  */
	def specificTargetParentIds_=(newParentId: Int)(implicit connection: Connection) = 
		putColumn(specificTargetModel.parentIdColumn, newParentId)
	
	/**
	  * Updates the ratios of the targeted type specific allocation targets
	  * @param newRatio A new ratio to assign
	  * @return Whether any type specific allocation target was affected
	  */
	def specificTargetRatios_=(newRatio: Double)(implicit connection: Connection) = 
		putColumn(specificTargetModel.ratioColumn, newRatio)
	
	/**
	  * Updates the type ids of the targeted type specific allocation targets
	  * @param newTypeId A new type id to assign
	  * @return Whether any type specific allocation target was affected
	  */
	def specificTargetTypeIds_=(newTypeId: Int)(implicit connection: Connection) = 
		putColumn(specificTargetModel.typeIdColumn, newTypeId)
}

