package vf.arbiter.core.database.access.single.invoice

import utopia.flow.datastructure.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.invoice.UnitCategoryFactory
import vf.arbiter.core.database.model.invoice.UnitCategoryModel
import vf.arbiter.core.model.stored.invoice.UnitCategory

/**
  * A common trait for access points that return individual and distinct UnitCategories.
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
trait UniqueUnitCategoryAccess 
	extends SingleRowModelAccess[UnitCategory] 
		with DistinctModelAccess[UnitCategory, Option[UnitCategory], Value] with Indexed
{
	// COMPUTED	--------------------
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = UnitCategoryModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = UnitCategoryFactory
}

