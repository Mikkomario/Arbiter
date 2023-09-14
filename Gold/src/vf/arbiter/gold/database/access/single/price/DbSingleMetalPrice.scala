package vf.arbiter.gold.database.access.single.price

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.gold.model.stored.price.MetalPrice

/**
  * An access point to individual metal prices, based on their id
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
case class DbSingleMetalPrice(id: Int) extends UniqueMetalPriceAccess with SingleIntIdModelAccess[MetalPrice]

