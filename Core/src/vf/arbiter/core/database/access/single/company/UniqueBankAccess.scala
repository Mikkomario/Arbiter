package vf.arbiter.core.database.access.single.company

import java.time.Instant
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
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
  * @since 2021-10-31
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
	
	/**
	  * Id of the user who registered this address. None if no instance (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this Bank was first created. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
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
	  * Updates the created of the targeted Bank instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any Bank instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted Bank instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any Bank instance was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the name of the targeted Bank instance(s)
	  * @param newName A new name to assign
	  * @return Whether any Bank instance was affected
	  */
	def name_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
}

