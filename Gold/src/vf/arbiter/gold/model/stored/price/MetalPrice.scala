package vf.arbiter.gold.model.stored.price

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.gold.database.access.single.price.DbSingleMetalPrice
import vf.arbiter.gold.model.partial.price.MetalPriceData

/**
  * Represents a metal price that has already been stored in the database
  * @param id id of this metal price in the database
  * @param data Wrapped metal price data
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
case class MetalPrice(id: Int, data: MetalPriceData) extends StoredModelConvertible[MetalPriceData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this metal price in the database
	  */
	def access = DbSingleMetalPrice(id)
}

