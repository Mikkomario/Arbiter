package vf.arbiter.accounting.database.access.single.transaction

import utopia.citadel.database.access.single.description.SingleIdDescribedAccess
import vf.arbiter.accounting.database.access.many.description.DbTransactionDescriptions
import vf.arbiter.accounting.database.access.single.description.DbTransactionDescriptionDescription
import vf.arbiter.accounting.model.combined.transaction.DescribedTransaction
import vf.arbiter.accounting.model.stored.transaction.Transaction

/**
  * An access point to individual transactions, based on their id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class DbSingleTransaction(id: Int) 
	extends UniqueTransactionAccess with SingleIdDescribedAccess[Transaction, DescribedTransaction]
{
	// IMPLEMENTED	--------------------
	
	override protected def describedFactory = DescribedTransaction
	
	override protected def manyDescriptionsAccess = DbTransactionDescriptions
	
	override protected def singleDescriptionAccess = DbTransactionDescriptionDescription
}

