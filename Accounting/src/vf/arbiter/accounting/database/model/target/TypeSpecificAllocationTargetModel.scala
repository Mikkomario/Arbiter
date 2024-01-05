package vf.arbiter.accounting.database.model.target

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.accounting.database.factory.target.TypeSpecificAllocationTargetFactory
import vf.arbiter.accounting.model.partial.target.TypeSpecificAllocationTargetData
import vf.arbiter.accounting.model.stored.target.TypeSpecificAllocationTarget

/**
  * 
	Used for constructing TypeSpecificAllocationTargetModel instances and for inserting type specific allocation
  *  targets to the database
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object TypeSpecificAllocationTargetModel 
	extends DataInserter[TypeSpecificAllocationTargetModel, TypeSpecificAllocationTarget, 
		TypeSpecificAllocationTargetData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains type specific allocation target parent id
	  */
	val parentIdAttName = "parentId"
	
	/**
	  * Name of the property that contains type specific allocation target type id
	  */
	val typeIdAttName = "typeId"
	
	/**
	  * Name of the property that contains type specific allocation target ratio
	  */
	val ratioAttName = "ratio"
	
	/**
	  * Name of the property that contains type specific allocation target is maximum
	  */
	val isMaximumAttName = "isMaximum"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains type specific allocation target parent id
	  */
	def parentIdColumn = table(parentIdAttName)
	
	/**
	  * Column that contains type specific allocation target type id
	  */
	def typeIdColumn = table(typeIdAttName)
	
	/**
	  * Column that contains type specific allocation target ratio
	  */
	def ratioColumn = table(ratioAttName)
	
	/**
	  * Column that contains type specific allocation target is maximum
	  */
	def isMaximumColumn = table(isMaximumAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = TypeSpecificAllocationTargetFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: TypeSpecificAllocationTargetData) = 
		apply(None, Some(data.parentId), Some(data.typeId), Some(data.ratio), Some(data.isMaximum))
	
	override protected def complete(id: Value, data: TypeSpecificAllocationTargetData) = 
		TypeSpecificAllocationTarget(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param id A type specific allocation target id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param isMaximum Whether this target represents the largest allowed value. False if this represents
	  *  the minimum target.
	  * @return A model containing only the specified is maximum
	  */
	def withIsMaximum(isMaximum: Boolean) = apply(isMaximum = Some(isMaximum))
	
	/**
	  * @param parentId Id of the target to which this specific value belongs
	  * @return A model containing only the specified parent id
	  */
	def withParentId(parentId: Int) = apply(parentId = Some(parentId))
	
	/**
	  * @param ratio The targeted ratio of total after-expenses income that should be allocated into
	  *  this transaction type. 
	  * If a ratio has been specified for a parent transaction type, 
	  * 
		this ratio is applied to the parent's portion (E.g. ratio of 1.0 would allocate 100% of the parent's share to this child type). 
	  * [0,1]
	  * @return A model containing only the specified ratio
	  */
	def withRatio(ratio: Double) = apply(ratio = Some(ratio))
	
	/**
	  * @param typeId Id of the allocated transaction type
	  * @return A model containing only the specified type id
	  */
	def withTypeId(typeId: Int) = apply(typeId = Some(typeId))
}

/**
  * Used for interacting with TypeSpecificAllocationTargets in the database
  * @param id type specific allocation target database id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class TypeSpecificAllocationTargetModel(id: Option[Int] = None, parentId: Option[Int] = None, 
	typeId: Option[Int] = None, ratio: Option[Double] = None, isMaximum: Option[Boolean] = None) 
	extends StorableWithFactory[TypeSpecificAllocationTarget]
{
	// IMPLEMENTED	--------------------
	
	override def factory = TypeSpecificAllocationTargetModel.factory
	
	override def valueProperties = {
		import TypeSpecificAllocationTargetModel._
		Vector("id" -> id, parentIdAttName -> parentId, typeIdAttName -> typeId, ratioAttName -> ratio, 
			isMaximumAttName -> isMaximum)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param isMaximum Whether this target represents the largest allowed value. False if this represents
	  *  the minimum target.
	  * @return A new copy of this model with the specified is maximum
	  */
	def withIsMaximum(isMaximum: Boolean) = copy(isMaximum = Some(isMaximum))
	
	/**
	  * @param parentId Id of the target to which this specific value belongs
	  * @return A new copy of this model with the specified parent id
	  */
	def withParentId(parentId: Int) = copy(parentId = Some(parentId))
	
	/**
	  * @param ratio The targeted ratio of total after-expenses income that should be allocated into
	  *  this transaction type. 
	  * If a ratio has been specified for a parent transaction type, 
	  * 
		this ratio is applied to the parent's portion (E.g. ratio of 1.0 would allocate 100% of the parent's share to this child type). 
	  * [0,1]
	  * @return A new copy of this model with the specified ratio
	  */
	def withRatio(ratio: Double) = copy(ratio = Some(ratio))
	
	/**
	  * @param typeId Id of the allocated transaction type
	  * @return A new copy of this model with the specified type id
	  */
	def withTypeId(typeId: Int) = copy(typeId = Some(typeId))
}

