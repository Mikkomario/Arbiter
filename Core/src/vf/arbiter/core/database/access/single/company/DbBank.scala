package vf.arbiter.core.database.access.single.company

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.company.BankFactory
import vf.arbiter.core.database.model.company.BankModel
import vf.arbiter.core.model.stored.company.Bank

/**
  * Used for accessing individual Banks
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object DbBank extends SingleRowModelAccess[Bank] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = BankModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = BankFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted Bank instance
	  * @return An access point to that Bank
	  */
	def apply(id: Int) = DbSingleBank(id)
}

