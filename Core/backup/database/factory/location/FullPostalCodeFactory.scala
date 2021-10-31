package vf.arbiter.core.database.factory.location

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import vf.arbiter.core.model.combined.location.FullPostalCode
import vf.arbiter.core.model.stored.location.{County, PostalCode}

/**
  * Used for reading FullPostalCodes from the database
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object FullPostalCodeFactory extends CombiningFactory[FullPostalCode, PostalCode, County]
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = CountyFactory
	
	override def parentFactory = PostalCodeFactory
	
	override def apply(code: PostalCode, county: County) = FullPostalCode(code, county)
}

