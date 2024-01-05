package vf.arbiter.accounting.database.access.many.transaction.party_entry

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.PartyEntryFactory
import vf.arbiter.accounting.database.model.transaction.PartyEntryModel
import vf.arbiter.accounting.model.stored.transaction.PartyEntry

import java.time.Instant

object ManyPartyEntriesAccess
{
	// NESTED	--------------------
	
	private class ManyPartyEntriesSubView(condition: Condition) extends ManyPartyEntriesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple party entries at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait ManyPartyEntriesAccess 
	extends ManyRowModelAccess[PartyEntry] with FilterableView[ManyPartyEntriesAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * names of the accessible party entries
	  */
	def names(implicit connection: Connection) = pullColumn(model.nameColumn).flatMap { _.string }
	
	/**
	  * creation times of the accessible party entries
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PartyEntryModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PartyEntryFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyPartyEntriesAccess = 
		new ManyPartyEntriesAccess.ManyPartyEntriesSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted party entries
	  * @param newCreated A new created to assign
	  * @return Whether any party entry was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the names of the targeted party entries
	  * @param newName A new name to assign
	  * @return Whether any party entry was affected
	  */
	def names_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
}

