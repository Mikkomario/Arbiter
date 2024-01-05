package vf.arbiter.accounting.database.access.single.transaction

import utopia.citadel.database.access.single.description.SingleIdDescribedAccess
import vf.arbiter.accounting.database.access.many.description.DbTransactionTypeDescriptions
import vf.arbiter.accounting.database.access.single.description.DbTransactionTypeDescriptionDescription
import vf.arbiter.accounting.model.combined.transaction.DescribedTransactionType
import vf.arbiter.accounting.model.stored.transaction.TransactionType

/**
  * An access point to individual transaction types, based on their id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class DbSingleTransactionType(id: Int) 
	extends UniqueTransactionTypeAccess 
		with SingleIdDescribedAccess[TransactionType, DescribedTransactionType]
{
	// IMPLEMENTED	--------------------
	
	override protected def describedFactory = DescribedTransactionType
	
	override protected def manyDescriptionsAccess = DbTransactionTypeDescriptions
	
	override protected def singleDescriptionAccess = DbTransactionTypeDescriptionDescription
}

