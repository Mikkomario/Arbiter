package vf.arbiter.core.database.model.company

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
  * @since 2021-10-10
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
	  * The factory object used by this model type
	  */
	def factory = BankFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: BankData) = apply(None, Some(data.name), Some(data.bic))
	
	override def complete(id: Value, data: BankData) = Bank(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param bic BIC-code of this bank
	  * @return A model containing only the specified bic
	  */
	def withBic(bic: String) = apply(bic = Some(bic))
	
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
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class BankModel(id: Option[Int] = None, name: Option[String] = None, bic: Option[String] = None) 
	extends StorableWithFactory[Bank]
{
	// IMPLEMENTED	--------------------
	
	override def factory = BankModel.factory
	
	override def valueProperties = 
	{
		import BankModel._
		Vector("id" -> id, nameAttName -> name, bicAttName -> bic)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param bic A new bic
	  * @return A new copy of this model with the specified bic
	  */
	def withBic(bic: String) = copy(bic = Some(bic))
	
	/**
	  * @param name A new name
	  * @return A new copy of this model with the specified name
	  */
	def withName(name: String) = copy(name = Some(name))
}

