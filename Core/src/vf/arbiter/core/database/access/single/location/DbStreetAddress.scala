package vf.arbiter.core.database.access.single.location

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.location.StreetAddressFactory
import vf.arbiter.core.database.model.location.StreetAddressModel
import vf.arbiter.core.model.stored.location.StreetAddress

/**
  * Used for accessing individual StreetAddresss
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object DbStreetAddress extends SingleRowModelAccess[StreetAddress] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = StreetAddressModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = StreetAddressFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted StreetAddress instance
	  * @return An access point to that StreetAddress
	  */
	def apply(id: Int) = new DbSingleStreetAddress(id)
	
	
	// NESTED	--------------------
	
	class DbSingleStreetAddress(val id: Int) 
		extends UniqueStreetAddressAccess with UniqueModelAccess[StreetAddress]
	{
		// IMPLEMENTED	--------------------
		
		override def condition = index <=> id
	}
}

