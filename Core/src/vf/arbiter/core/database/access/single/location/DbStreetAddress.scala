package vf.arbiter.core.database.access.single.location

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.location.StreetAddressFactory
import vf.arbiter.core.database.model.location.StreetAddressModel
import vf.arbiter.core.model.partial.location.StreetAddressData
import vf.arbiter.core.model.stored.location.StreetAddress

/**
  * Used for accessing individual StreetAddresses
  * @author Mikko Hilpinen
  * @since 2021-10-14
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
	def apply(id: Int) = DbSingleStreetAddress(id)
	
	/**
	 * Inserts a new street address, except if there already exists an identical copy
	 * @param addressData Street address data
	 * @param connection Implicit DB Connection
	 * @return Existing or new street address
	 */
	def getOrInsert(addressData: StreetAddressData)(implicit connection: Connection) =
		find(model(addressData).toCondition).getOrElse { model.insert(addressData) }
}

