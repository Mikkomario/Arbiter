package vf.arbiter.accounting.database.access.many.description

import utopia.citadel.database.access.many.description.LinkedDescriptionsAccess
import vf.arbiter.accounting.database.factory.description.ArbiterAccountingLinkedDescriptionFactory
import vf.arbiter.accounting.database.model.description.ArbiterAccountingDescriptionLinkModel

/**
  * Used for accessing multiple transaction descriptions at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbTransactionDescriptions extends LinkedDescriptionsAccess
{
	// IMPLEMENTED	--------------------
	
	override def factory = ArbiterAccountingLinkedDescriptionFactory.transaction
	
	override def linkModel = ArbiterAccountingDescriptionLinkModel.transaction
}

