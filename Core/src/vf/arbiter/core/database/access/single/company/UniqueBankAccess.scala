package vf.arbiter.core.database.access.single.company

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.company.BankFactory
import vf.arbiter.core.database.model.company.BankModel
import vf.arbiter.core.model.stored.company.Bank

/**
  * A common trait for access points that return individual and distinct Banks.
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait UniqueBankAccess 
	extends SingleRowModelAccess[Bank] with DistinctModelAccess[Bank, Option[Bank], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * (Short) name of this bank. None if no instance (or value) was found.
	  */
	def name(implicit connection: Connection) = pullColumn(model.nameColumn).string
	
	/**
	  * BIC-code of this bank. None if no instance (or value) was found.
	  */
	def bic(implicit connection: Connection) = pullColumn(model.bicColumn).string
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = BankModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = BankFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the bic of the targeted Bank instance(s)
	  * @param newBic A new bic to assign
	  * @return Whether any Bank instance was affected
	  */
	def bic_=(newBic: String)(implicit connection: Connection) = putColumn(model.bicColumn, newBic)
	
	/**
	  * Updates the name of the targeted Bank instance(s)
	  * @param newName A new name to assign
	  * @return Whether any Bank instance was affected
	  */
	def name_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
}

