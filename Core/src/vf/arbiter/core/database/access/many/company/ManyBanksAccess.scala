package vf.arbiter.core.database.access.many.company

import java.time.Instant
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, SubView}
import utopia.vault.sql.Condition
import vf.arbiter.core.database.factory.company.BankFactory
import vf.arbiter.core.database.model.company.BankModel
import vf.arbiter.core.model.stored.company.Bank

object ManyBanksAccess
{
	// NESTED	--------------------
	
	private class ManyBanksSubView(override val parent: ManyRowModelAccess[Bank], 
		override val filterCondition: Condition) 
		extends ManyBanksAccess with SubView
}

/**
  * A common trait for access points which target multiple Banks at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait ManyBanksAccess extends ManyRowModelAccess[Bank] with Indexed with FilterableView[ManyBanksAccess]
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
	
	/**
	  * creatorIds of the accessible Banks
	  */
	def creatorIds(implicit connection: Connection) = 
		pullColumn(model.creatorIdColumn).flatMap { value => value.int }
	
	/**
	  * creationTimes of the accessible Banks
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = BankModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = BankFactory
	
	override def filter(additionalCondition: Condition): ManyBanksAccess = 
		new ManyBanksAccess.ManyBanksSubView(this, additionalCondition)
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the bic of the targeted Bank instance(s)
	  * @param newBic A new bic to assign
	  * @return Whether any Bank instance was affected
	  */
	def bics_=(newBic: String)(implicit connection: Connection) = putColumn(model.bicColumn, newBic)
	
	/**
	  * Updates the created of the targeted Bank instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any Bank instance was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted Bank instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any Bank instance was affected
	  */
	def creatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the name of the targeted Bank instance(s)
	  * @param newName A new name to assign
	  * @return Whether any Bank instance was affected
	  */
	def names_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
}

