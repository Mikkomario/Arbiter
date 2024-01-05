package vf.arbiter.accounting.database.access.single.transaction.party_entry

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.arbiter.accounting.model.stored.transaction.PartyEntry

/**
  * An access point to individual party entries, based on their id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class DbSinglePartyEntry(id: Int) extends UniquePartyEntryAccess with SingleIntIdModelAccess[PartyEntry]

