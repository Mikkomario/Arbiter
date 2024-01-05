package vf.arbiter.accounting.database.access.single.transaction.party_entry

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.PartyEntryFactory
import vf.arbiter.accounting.database.model.transaction.PartyEntryModel
import vf.arbiter.accounting.model.stored.transaction.PartyEntry

import java.time.Instant

object UniquePartyEntryAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniquePartyEntryAccess = new _UniquePartyEntryAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniquePartyEntryAccess(condition: Condition) extends UniquePartyEntryAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct party entries.
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait UniquePartyEntryAccess 
	extends SingleRowModelAccess[PartyEntry] with FilterableView[UniquePartyEntryAccess] 
		with DistinctModelAccess[PartyEntry, Option[PartyEntry], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Name of this entity, 
		just as it appeared on a bank statement. None if no party entry (or value) was found.
	  */
	def name(implicit connection: Connection) = pullColumn(model.nameColumn).getString
	
	/**
	  * Time when this party entry was added to the database. None if no party entry (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PartyEntryModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PartyEntryFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniquePartyEntryAccess = 
		new UniquePartyEntryAccess._UniquePartyEntryAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted party entries
	  * @param newCreated A new created to assign
	  * @return Whether any party entry was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the names of the targeted party entries
	  * @param newName A new name to assign
	  * @return Whether any party entry was affected
	  */
	def name_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
}

