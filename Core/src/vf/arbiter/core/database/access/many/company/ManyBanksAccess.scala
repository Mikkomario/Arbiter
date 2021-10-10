package vf.arbiter.core.database.access.many.company

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.company.BankFactory
import vf.arbiter.core.database.model.company.BankModel
import vf.arbiter.core.model.stored.company.Bank

/**
  * A common trait for access points which target multiple Banks at a time
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait ManyBanksAccess extends ManyRowModelAccess[Bank] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * names of the accessible Banks
	  */
	def names(implicit connection: Connection) = pullColumn(model.nameColumn)
		.flatMap { value => value.string }
	
	/**
	  * bics of the accessible Banks
	  */
	def bics(implicit connection: Connection) = pullColumn(model.bicColumn).flatMap { value => value.string }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = BankModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = BankFactory
	
	override protected def defaultOrdering = None
	
	
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

