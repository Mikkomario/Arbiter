package vf.arbiter.core.database.access.single.location

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.location.PostalCodeFactory
import vf.arbiter.core.database.model.location.PostalCodeModel
import vf.arbiter.core.model.stored.location.PostalCode

/**
  * Used for accessing individual PostalCodes
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object DbPostalCode extends SingleRowModelAccess[PostalCode] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PostalCodeModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PostalCodeFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted PostalCode instance
	  * @return An access point to that PostalCode
	  */
	def apply(id: Int) = new DbSinglePostalCode(id)
	
	
	// NESTED	--------------------
	
	class DbSinglePostalCode(val id: Int) extends UniquePostalCodeAccess with UniqueModelAccess[PostalCode]
	{
		// IMPLEMENTED	--------------------
		
		override def condition = index <=> id
	}
}

