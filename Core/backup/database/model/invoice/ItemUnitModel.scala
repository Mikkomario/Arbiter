package vf.arbiter.core.database.model.invoice

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.invoice.ItemUnitFactory
import vf.arbiter.core.model.partial.invoice.ItemUnitData
import vf.arbiter.core.model.stored.invoice.ItemUnit

/**
  * Used for constructing ItemUnitModel instances and for inserting ItemUnits to the database
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object ItemUnitModel extends DataInserter[ItemUnitModel, ItemUnit, ItemUnitData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains ItemUnit categoryId
	  */
	val categoryIdAttName = "categoryId"
	
	/**
	  * Name of the property that contains ItemUnit multiplier
	  */
	val multiplierAttName = "multiplier"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains ItemUnit categoryId
	  */
	def categoryIdColumn = table(categoryIdAttName)
	
	/**
	  * Column that contains ItemUnit multiplier
	  */
	def multiplierColumn = table(multiplierAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = ItemUnitFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: ItemUnitData) = apply(None, Some(data.categoryId), Some(data.multiplier))
	
	override def complete(id: Value, data: ItemUnitData) = ItemUnit(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param categoryId Id of the category this unit belongs to
	  * @return A model containing only the specified categoryId
	  */
	def withCategoryId(categoryId: Int) = apply(categoryId = Some(categoryId))
	
	/**
	  * @param id A ItemUnit id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param multiplier A multiplier that, when applied to this unit, makes it comparable 
		with the other units in the same category
	  * @return A model containing only the specified multiplier
	  */
	def withMultiplier(multiplier: Double) = apply(multiplier = Some(multiplier))
}

/**
  * Used for interacting with ItemUnits in the database
  * @param id ItemUnit database id
  * @param categoryId Id of the category this unit belongs to
  * @param multiplier A multiplier that, when applied to this unit, makes it comparable 
	with the other units in the same category
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
case class ItemUnitModel(id: Option[Int] = None, categoryId: Option[Int] = None, 
	multiplier: Option[Double] = None) 
	extends StorableWithFactory[ItemUnit]
{
	// IMPLEMENTED	--------------------
	
	override def factory = ItemUnitModel.factory
	
	override def valueProperties = 
	{
		import ItemUnitModel._
		Vector("id" -> id, categoryIdAttName -> categoryId, multiplierAttName -> multiplier)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param categoryId A new categoryId
	  * @return A new copy of this model with the specified categoryId
	  */
	def withCategoryId(categoryId: Int) = copy(categoryId = Some(categoryId))
	
	/**
	  * @param multiplier A new multiplier
	  * @return A new copy of this model with the specified multiplier
	  */
	def withMultiplier(multiplier: Double) = copy(multiplier = Some(multiplier))
}

