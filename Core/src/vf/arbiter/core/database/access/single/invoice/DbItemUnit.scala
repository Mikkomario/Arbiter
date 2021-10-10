package vf.arbiter.core.database.access.single.invoice

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.invoice.ItemUnitFactory
import vf.arbiter.core.database.model.invoice.ItemUnitModel
import vf.arbiter.core.model.stored.invoice.ItemUnit

/**
  * Used for accessing individual ItemUnits
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object DbItemUnit extends SingleRowModelAccess[ItemUnit] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = ItemUnitModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ItemUnitFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted ItemUnit instance
	  * @return An access point to that ItemUnit
	  */
	def apply(id: Int) = new DbSingleItemUnit(id)
	
	
	// NESTED	--------------------
	
	class DbSingleItemUnit(val id: Int) extends UniqueItemUnitAccess with UniqueModelAccess[ItemUnit]
	{
		// IMPLEMENTED	--------------------
		
		override def condition = index <=> id
	}
}

