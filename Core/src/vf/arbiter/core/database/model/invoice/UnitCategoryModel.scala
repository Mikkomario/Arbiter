package vf.arbiter.core.database.model.invoice

import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.invoice.UnitCategoryFactory
import vf.arbiter.core.model.partial.invoice.UnitCategoryData
import vf.arbiter.core.model.stored.invoice.UnitCategory

/**
  * Used for constructing UnitCategoryModel instances and for inserting UnitCategorys to the database
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object UnitCategoryModel extends DataInserter[UnitCategoryModel, UnitCategory, UnitCategoryData]
{
	// COMPUTED	--------------------
	
	/**
	  * The factory object used by this model type
	  */
	def factory = UnitCategoryFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: UnitCategoryData) = apply(None)
	
	override def complete(id: Value, data: UnitCategoryData) = UnitCategory(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param id A UnitCategory id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
}

/**
  * Used for interacting with UnitCategories in the database
  * @param id UnitCategory database id
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class UnitCategoryModel(id: Option[Int] = None) extends StorableWithFactory[UnitCategory]
{
	// IMPLEMENTED	--------------------
	
	override def factory = UnitCategoryModel.factory
	
	override def valueProperties = Vector("id" -> id)
}

