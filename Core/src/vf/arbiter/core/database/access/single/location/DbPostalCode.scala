package vf.arbiter.core.database.access.single.location

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.location.PostalCodeFactory
import vf.arbiter.core.database.model.location.PostalCodeModel
import vf.arbiter.core.model.combined.location.FullPostalCode
import vf.arbiter.core.model.partial.location.PostalCodeData
import vf.arbiter.core.model.stored.location.PostalCode

/**
  * Used for accessing individual PostalCodes
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbPostalCode extends SingleRowModelAccess[PostalCode] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PostalCodeModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PostalCodeFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted PostalCode instance
	  * @return An access point to that PostalCode
	  */
	def apply(id: Int) = DbSinglePostalCode(id)
	
	/**
	 * Finds an existing postal code or inserts a new one
	 * @param countyId Id of the county where this code resides
	 * @param code Postal code
	 * @param creatorId Id of the user registered as creator (if known & needed) (call-by-name)
	 * @param connection Implicit DB Connection
	 * @return Existing or new postal code
	 */
	def getOrInsert(countyId: Int, code: String, creatorId: => Option[Int])
	               (implicit connection: Connection) =
		find(model.withCountyId(countyId).withNumber(code).toCondition)
			.getOrElse { model.insert(PostalCodeData(code, countyId, creatorId)) }
	/**
	 * Finds an existing postal code or inserts a new one
	 * @param countyName Name of the county where this code resides
	 * @param code Postal code
	 * @param creatorId Id of the user who gave this information (if applicable & needed, call-by-name)
	 * @param connection Implicit DB Connection
	 * @return Existing or new postal code, with county data included
	 */
	def getOrInsert(countyName: String, code: String, creatorId: => Option[Int] = None)
	               (implicit connection: Connection): FullPostalCode =
	{
		val county = DbCounty.getOrInsert(countyName, creatorId)
		val postal = getOrInsert(county.id, code, creatorId)
		FullPostalCode(postal, county)
	}
}

