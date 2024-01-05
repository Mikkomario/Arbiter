package vf.arbiter.accounting.model.stored.transaction

import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.accounting.database.access.single.transaction.party_entry.DbSinglePartyEntry
import vf.arbiter.accounting.model.partial.transaction.PartyEntryData

/**
  * Represents a party entry that has already been stored in the database
  * @param id id of this party entry in the database
  * @param data Wrapped party entry data
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class PartyEntry(id: Int, data: PartyEntryData) extends StoredModelConvertible[PartyEntryData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this party entry in the database
	  */
	def access = DbSinglePartyEntry(id)
}

