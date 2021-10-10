package vf.arbiter.core.database.access.many.description

import utopia.citadel.database.access.many.description.DbDescriptions.{DescriptionsOfAll, DescriptionsOfMany, DescriptionsOfSingle}
import vf.arbiter.core.database.factory.CoreDescriptionLinkFactory
import vf.arbiter.core.database.model.CoreDescriptionLinkModel

object DbItemUnitDescriptions
{
	// ATTRIBUTES	--------------------
	
	/**
	  * An access point to the descriptions of all ItemUnits at once
	  */
	val all = DescriptionsOfAll(factory, model)
	
	
	// COMPUTED	--------------------
	
	/**
	  * Model factory used when interacting with ItemUnit description links
	  */
	def model = CoreDescriptionLinkModel.itemUnit
	
	/**
	  * Factory used when reading ItemUnit description links
	  */
	def factory = CoreDescriptionLinkFactory.itemUnit
	
	
	// OTHER	--------------------
	
	/**
	  * @param itemUnitId Id of the targeted ItemUnit
	  * @return An access point to that ItemUnit's descriptions
	  */
	def apply(itemUnitId: Int) = DescriptionsOfSingle(itemUnitId, factory, model)
	
	/**
	  * @param itemUnitIds Ids of the ItemUnits to target
	  * @return An access point to descriptions of the targeted ItemUnits
	  */
	def apply(itemUnitIds: Set[Int]) = DescriptionsOfMany(itemUnitIds, factory, model)
}

