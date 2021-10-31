package vf.arbiter.core.database.model.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.company.BankFactory
import vf.arbiter.core.model.partial.company.BankData
import vf.arbiter.core.model.stored.company.Bank

/**
  * Used for constructing BankModel instances and for inserting Banks to the database
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object BankModel extends DataInserter[BankModel, Bank, BankData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains Bank name
	  */
	val nameAttName = "name"
	
	/**
	  * Name of the property that contains Bank bic
	  */
	val bicAttName = "bic"
	
	/**
	  * Name of the property that contains Bank creatorId
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains Bank created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains Bank name
	  */
	def nameColumn = table(nameAttName)
	
	/**
	  * Column that contains Bank bic
	  */
	def bicColumn = table(bicAttName)
	
	/**
	  * Column that contains Bank creatorId
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains Bank created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = BankFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: BankData) = 
		apply(None, Some(data.name), Some(data.bic), data.creatorId, Some(data.created))
	
	override def complete(id: Value, data: BankData) = Bank(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param bic BIC-code of this bank
	  * @return A model containing only the specified bic
	  */
	def withBic(bic: String) = apply(bic = Some(bic))
	
	/**
	  * @param created Time when this Bank was first created
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who registered this address
	  * @return A model containing only the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param id A Bank id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param name (Short) name of this bank
	  * @return A model containing only the specified name
	  */
	def withName(name: String) = apply(name = Some(name))
}

/**
  * Used for interacting with Banks in the database
  * @param id Bank database id
  * @param name (Short) name of this bank
  * @param bic BIC-code of this bank
  * @param creatorId Id of the user who registered this address
  * @param created Time when this Bank was first created
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class BankModel(id: Option[Int] = None, name: Option[String] = None, bic: Option[String] = None, 
	creatorId: Option[Int] = None, created: Option[Instant] = None) 
	extends StorableWithFactory[Bank]
{
	// IMPLEMENTED	--------------------
	
	override def factory = BankModel.factory
	
	override def valueProperties = 
	{
		import BankModel._
		Vector("id" -> id, nameAttName -> name, bicAttName -> bic, creatorIdAttName -> creatorId, 
			createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param bic A new bic
	  * @return A new copy of this model with the specified bic
	  */
	def withBic(bic: String) = copy(bic = Some(bic))
	
	/**
	  * @param created A new created
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param creatorId A new creatorId
	  * @return A new copy of this model with the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = copy(creatorId = Some(creatorId))
	
	/**
	  * @param name A new name
	  * @return A new copy of this model with the specified name
	  */
	def withName(name: String) = copy(name = Some(name))
}

