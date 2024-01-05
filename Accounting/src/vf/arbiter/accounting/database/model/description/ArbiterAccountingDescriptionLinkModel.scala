package vf.arbiter.accounting.database.model.description

import utopia.citadel.database.model.description.DescriptionLinkModelFactory
import vf.arbiter.accounting.database.ArbiterAccountingTables

/**
  * Used for interacting with description links for models in arbiter accounting
  * @since 04.01.2024, v1.5
  */
object ArbiterAccountingDescriptionLinkModel
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Database interaction model factory for transaction description links
	  */
	lazy val transaction = DescriptionLinkModelFactory(ArbiterAccountingTables.transactionDescription)
	
	/**
	  * Database interaction model factory for transaction type description links
	  */
	lazy val transactionType = DescriptionLinkModelFactory(ArbiterAccountingTables.transactionTypeDescription)
}

