package vf.arbiter.accounting.database.factory.description

import utopia.citadel.database.factory.description.DescriptionLinkFactory
import vf.arbiter.accounting.database.ArbiterAccountingTables

/**
  * Used for accessing description link factories for the models in arbiter accounting
  * @since 04.01.2024, v1.5
  */
object ArbiterAccountingDescriptionLinkFactory
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Factory for reading transaction description links
	  */
	lazy val transaction = DescriptionLinkFactory(ArbiterAccountingTables.transactionDescription)
	
	/**
	  * Factory for reading transaction type description links
	  */
	lazy val transactionType = DescriptionLinkFactory(ArbiterAccountingTables.transactionTypeDescription)
}

