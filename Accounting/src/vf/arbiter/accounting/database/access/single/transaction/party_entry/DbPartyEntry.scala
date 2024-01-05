package vf.arbiter.accounting.database.access.single.transaction.party_entry

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.arbiter.accounting.database.factory.transaction.PartyEntryFactory
import vf.arbiter.accounting.database.model.transaction.PartyEntryModel
import vf.arbiter.accounting.model.stored.transaction.PartyEntry

/**
  * Used for accessing individual party entries
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbPartyEntry extends SingleRowModelAccess[PartyEntry] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PartyEntryModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PartyEntryFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted party entry
	  * @return An access point to that party entry
	  */
	def apply(id: Int) = DbSinglePartyEntry(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique party entries.
	  * @return An access point to the party entry that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniquePartyEntryAccess(mergeCondition(condition))
}

