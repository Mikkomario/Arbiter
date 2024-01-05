package vf.arbiter.accounting.model.combined.transaction

import utopia.metropolis.model.combined.description.{DescribedFactory, DescribedWrapper, LinkedDescription, SimplyDescribed}
import utopia.metropolis.model.stored.description.DescriptionRole
import vf.arbiter.accounting.model.stored.transaction.TransactionType

object DescribedTransactionType extends DescribedFactory[TransactionType, DescribedTransactionType]

/**
  * Combines transaction type with the linked descriptions
  * @param transactionType transaction type to wrap
  * @param descriptions Descriptions concerning the wrapped transaction type
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class DescribedTransactionType(transactionType: TransactionType, descriptions: Set[LinkedDescription]) 
	extends DescribedWrapper[TransactionType] with SimplyDescribed
{
	// IMPLEMENTED	--------------------
	
	override def wrapped = transactionType
	
	override protected def simpleBaseModel(roles: Iterable[DescriptionRole]) = wrapped.toModel
}

