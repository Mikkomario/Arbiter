package vf.arbiter.core.database.access.many.location

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.location.PostalCodeFactory
import vf.arbiter.core.database.model.location.PostalCodeModel
import vf.arbiter.core.model.stored.location.PostalCode

import java.time.Instant

object ManyPostalCodesAccess extends ViewFactory[ManyPostalCodesAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyPostalCodesAccess = _ManyPostalCodesAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyPostalCodesAccess(override val accessCondition: Option[Condition]) 
		extends ManyPostalCodesAccess
}

/**
  * A common trait for access points which target multiple PostalCodes at a time
  * @author Mikko Hilpinen
  * @since 31.10.2021
  */
trait ManyPostalCodesAccess 
	extends ManyRowModelAccess[PostalCode] with Indexed with FilterableView[ManyPostalCodesAccess]
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
	
	/**
	  * creatorIds of the accessible PostalCodes
	  */
	def creatorIds(implicit connection: Connection) = 
		pullColumn(model.creatorIdColumn).flatMap { value => value.int }
	
	/**
	  * creationTimes of the accessible PostalCodes
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PostalCodeModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PostalCodeFactory
	
	override def self = this
	
	override def apply(condition: Condition): ManyPostalCodesAccess = ManyPostalCodesAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the countyId of the targeted PostalCode instance(s)
	  * @param newCountyId A new countyId to assign
	  * @return Whether any PostalCode instance was affected
	  */
	def countyIds_=(newCountyId: Int)(implicit connection: Connection) = 
		putColumn(model.countyIdColumn, newCountyId)
	
	/**
	  * Updates the created of the targeted PostalCode instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any PostalCode instance was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted PostalCode instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any PostalCode instance was affected
	  */
	def creatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * @param countyIds Ids of targeted counties
	  * @return A copy of this access point, limited to those counties
	  */
	def inCountiesWithIds(countyIds: Iterable[Int]) = filter(model.countyIdColumn in countyIds)
	
	/**
	  * Updates the number of the targeted PostalCode instance(s)
	  * @param newNumber A new number to assign
	  * @return Whether any PostalCode instance was affected
	  */
	def numbers_=(newNumber: String)(implicit connection: Connection) = putColumn(model.numberColumn,
		newNumber)
	
	/**
	  * @param postalCodes Targeted postal codes / numbers
	  * 
		@return A copy of this access point limited to those codes (but not necessarily containing all of them)
	  */
	def withAnyOfCodes(postalCodes: Iterable[String]) = filter(model.numberColumn in postalCodes)
}

