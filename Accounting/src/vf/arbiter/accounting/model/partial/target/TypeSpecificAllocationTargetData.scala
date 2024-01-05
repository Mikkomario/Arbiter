package vf.arbiter.accounting.model.partial.target

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.BooleanType
import utopia.flow.generic.model.mutable.DataType.DoubleType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible

object TypeSpecificAllocationTargetData extends FromModelFactoryWithSchema[TypeSpecificAllocationTargetData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("parentId", IntType, Vector("parent_id")), 
			PropertyDeclaration("typeId", IntType, Vector("type_id")), PropertyDeclaration("ratio", 
			DoubleType), PropertyDeclaration("isMaximum", BooleanType, Vector("is_maximum"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		TypeSpecificAllocationTargetData(valid("parentId").getInt, valid("typeId").getInt, 
			valid("ratio").getDouble, valid("isMaximum").getBoolean)
}

/**
  * Represents an allocation / target for a specific transaction-type
  * @param parentId Id of the target to which this specific value belongs
  * @param typeId Id of the allocated transaction type
  * @param ratio The targeted ratio of total after-expenses income that should be allocated into
  *  this transaction type. 
  * If a ratio has been specified for a parent transaction type, 
  * 
	this ratio is applied to the parent's portion (E.g. ratio of 1.0 would allocate 100% of the parent's share to this child type). 
  * [0,1]
  * @param isMaximum Whether this target represents the largest allowed value. False if this represents
  *  the minimum target.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class TypeSpecificAllocationTargetData(parentId: Int, typeId: Int, ratio: Double, 
	isMaximum: Boolean = false) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("parentId" -> parentId, "typeId" -> typeId, "ratio" -> ratio, "isMaximum" -> isMaximum))
}

