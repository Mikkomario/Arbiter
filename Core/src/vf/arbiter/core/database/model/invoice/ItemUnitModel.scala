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
  * @since 2021-10-11
  */
object ItemUnitModel extends DataInserter[ItemUnitModel, ItemUnit, ItemUnitData]
{
	// COMPUTED	--------------------
	
	/**
	  * The factory object used by this model type
	  */
	def factory = ItemUnitFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: ItemUnitData) = apply(None)
	
	override def complete(id: Value, data: ItemUnitData) = ItemUnit(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param id A ItemUnit id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
}

/**
  * Used for interacting with ItemUnits in the database
  * @param id ItemUnit database id
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
case class ItemUnitModel(id: Option[Int] = None) extends StorableWithFactory[ItemUnit]
{
	// IMPLEMENTED	--------------------
	
	override def factory = ItemUnitModel.factory
	
	override def valueProperties = Vector("id" -> id)
}

