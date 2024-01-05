package vf.arbiter.accounting.model.combined.transaction

import utopia.metropolis.model.combined.description.{DescribedFactory, DescribedWrapper, LinkedDescription, SimplyDescribed}
import utopia.metropolis.model.stored.description.DescriptionRole
import vf.arbiter.accounting.model.stored.transaction.Transaction

object DescribedTransaction extends DescribedFactory[Transaction, DescribedTransaction]

/**
  * Combines transaction with the linked descriptions
  * @param transaction transaction to wrap
  * @param descriptions Descriptions concerning the wrapped transaction
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class DescribedTransaction(transaction: Transaction, descriptions: Set[LinkedDescription]) 
	extends DescribedWrapper[Transaction] with SimplyDescribed
{
	// IMPLEMENTED	--------------------
	
	override def wrapped = transaction
	
	override protected def simpleBaseModel(roles: Iterable[DescriptionRole]) = wrapped.toModel
}

