package vf.arbiter.core.database.access.single.location

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.location.PostalCodeFactory
import vf.arbiter.core.database.model.location.PostalCodeModel
import vf.arbiter.core.model.partial.location.PostalCodeData
import vf.arbiter.core.model.stored.location.PostalCode

import scala.annotation.tailrec

/**
  * Used for accessing individual PostalCodes
  * @author Mikko Hilpinen
  * @since 2021-10-10
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
	def apply(id: Int) = new DbSinglePostalCode(id)
	
	/**
	 * Finds an existing postal code or inserts a new one
	 * @param countyId Id of the county where this code resides
	 * @param code Postal code
	 * @param connection Implicit DB Connection
	 * @return Existing or new postal code
	 */
	def getOrInsert(countyId: Int, code: String)(implicit connection: Connection) =
		find(model.withCountyId(countyId).withNumber(code).toCondition)
			.getOrElse { model.insert(PostalCodeData(code, countyId)) }
	/**
	 * Finds an existing postal code or inserts a new one
	 * @param countyName Name of the county where this code resides
	 * @param code Postal code
	 * @param connection Implicit DB Connection
	 * @return Existing or new postal code
	 */
	def getOrInsert(countyName: String, code: String)(implicit connection: Connection): PostalCode =
		getOrInsert(DbCounty.withNameOrInsert(countyName).id, code)
	
	
	// NESTED	--------------------
	
	class DbSinglePostalCode(val id: Int) extends UniquePostalCodeAccess with UniqueModelAccess[PostalCode]
	{
		// IMPLEMENTED	--------------------
		
		override def condition = index <=> id
	}
}

