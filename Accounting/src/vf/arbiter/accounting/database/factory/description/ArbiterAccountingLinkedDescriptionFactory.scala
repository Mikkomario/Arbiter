package vf.arbiter.accounting.database.factory.description

import utopia.citadel.database.factory.description.LinkedDescriptionFactory

/**
  * Used for reading linked descriptions for models in arbiter accounting
  * @since 04.01.2024, v1.5
  */
object ArbiterAccountingLinkedDescriptionFactory
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Factory for reading descriptions linked with transactions
	  */
	lazy val transaction = LinkedDescriptionFactory(ArbiterAccountingDescriptionLinkFactory.transaction)
	
	/**
	  * Factory for reading descriptions linked with transaction types
	  */
	lazy
		 val transactionType = LinkedDescriptionFactory(ArbiterAccountingDescriptionLinkFactory.transactionType)
}

