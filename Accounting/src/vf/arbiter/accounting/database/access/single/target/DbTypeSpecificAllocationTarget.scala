package vf.arbiter.accounting.database.access.single.target

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.target.TypeSpecificAllocationTargetFactory
import vf.arbiter.accounting.database.model.target.TypeSpecificAllocationTargetModel
import vf.arbiter.accounting.model.stored.target.TypeSpecificAllocationTarget

/**
  * Used for accessing individual type specific allocation targets
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbTypeSpecificAllocationTarget 
	extends SingleRowModelAccess[TypeSpecificAllocationTarget] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = TypeSpecificAllocationTargetModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TypeSpecificAllocationTargetFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted type specific allocation target
	  * @return An access point to that type specific allocation target
	  */
	def apply(id: Int) = DbSingleTypeSpecificAllocationTarget(id)
	
	/**
	  * 
		@param condition Filter condition to apply in addition to this root view's condition. Should yield unique type
	  *  specific allocation targets.
	  * @return An access point to the type specific allocation target that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = 
		UniqueTypeSpecificAllocationTargetAccess(mergeCondition(condition))
}

