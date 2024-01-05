package vf.arbiter.accounting.database.access.single.description

import utopia.citadel.database.access.single.description.LinkedDescriptionAccess
import vf.arbiter.accounting.database.factory.description.ArbiterAccountingLinkedDescriptionFactory
import vf.arbiter.accounting.database.model.description.ArbiterAccountingDescriptionLinkModel

/**
  * Used for accessing individual transaction descriptions
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object DbTransactionDescriptionDescription extends LinkedDescriptionAccess
{
	// IMPLEMENTED	--------------------
	
	override def factory = ArbiterAccountingLinkedDescriptionFactory.transaction
	
	override def linkModel = ArbiterAccountingDescriptionLinkModel.transaction
}

