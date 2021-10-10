package vf.arbiter.core.database.access.many.location

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.location.PostalCodeFactory
import vf.arbiter.core.database.model.location.PostalCodeModel
import vf.arbiter.core.model.stored.location.PostalCode

/**
  * A common trait for access points which target multiple PostalCodes at a time
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait ManyPostalCodesAccess extends ManyRowModelAccess[PostalCode] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * numbers of the accessible PostalCodes
	  */
	def numbers(implicit connection: Connection) = 
		pullColumn(model.numberColumn).flatMap { value => value.string }
	
	/**
	  * countyIds of the accessible PostalCodes
	  */
	def countyIds(implicit connection: Connection) = 
		pullColumn(model.countyIdColumn).flatMap { value => value.int }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PostalCodeModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PostalCodeFactory
	
	override protected def defaultOrdering = None
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the countyId of the targeted PostalCode instance(s)
	  * @param newCountyId A new countyId to assign
	  * @return Whether any PostalCode instance was affected
	  */
	def countyId_=(newCountyId: Int)(implicit connection: Connection) = 
		putColumn(model.countyIdColumn, newCountyId)
	
	/**
	  * Updates the number of the targeted PostalCode instance(s)
	  * @param newNumber A new number to assign
	  * @return Whether any PostalCode instance was affected
	  */
	def number_=(newNumber: String)(implicit connection: Connection) = putColumn(model.numberColumn, 
		newNumber)
}

