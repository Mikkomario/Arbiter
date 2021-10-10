package vf.arbiter.core.database.access.single.description

import utopia.citadel.database.access.single.description.DbDescription.DescriptionOfSingle
import vf.arbiter.core.database.factory.CoreDescriptionLinkFactory
import vf.arbiter.core.database.model.CoreDescriptionLinkModel

object DbItemUnitDescription
{
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
	  * @return Access point to the targeted ItemUnit's individual descriptions
	  */
	def apply(itemUnitId: Int) = DescriptionOfSingle(itemUnitId, factory, model)
}

