package vf.arbiter.accounting.database.model.transaction

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.accounting.database.factory.transaction.PartyEntryFactory
import vf.arbiter.accounting.model.partial.transaction.PartyEntryData
import vf.arbiter.accounting.model.stored.transaction.PartyEntry

import java.time.Instant

/**
  * Used for constructing PartyEntryModel instances and for inserting party entries to the database
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object PartyEntryModel extends DataInserter[PartyEntryModel, PartyEntry, PartyEntryData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains party entry name
	  */
	val nameAttName = "name"
	
	/**
	  * Name of the property that contains party entry created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains party entry name
	  */
	def nameColumn = table(nameAttName)
	
	/**
	  * Column that contains party entry created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = PartyEntryFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: PartyEntryData) = apply(None, data.name, Some(data.created))
	
	override protected def complete(id: Value, data: PartyEntryData) = PartyEntry(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this party entry was added to the database
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A party entry id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param name Name of this entity, just as it appeared on a bank statement
	  * @return A model containing only the specified name
	  */
	def withName(name: String) = apply(name = name)
}

/**
  * Used for interacting with PartyEntries in the database
  * @param id party entry database id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class PartyEntryModel(id: Option[Int] = None, name: String = "", created: Option[Instant] = None) 
	extends StorableWithFactory[PartyEntry]
{
	// IMPLEMENTED	--------------------
	
	override def factory = PartyEntryModel.factory
	
	override def valueProperties = {
		import PartyEntryModel._
		Vector("id" -> id, nameAttName -> name, createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this party entry was added to the database
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param name Name of this entity, just as it appeared on a bank statement
	  * @return A new copy of this model with the specified name
	  */
	def withName(name: String) = copy(name = name)
}

