package vf.arbiter.core.database.access.single.location

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import vf.arbiter.core.database.factory.location.CountyFactory
import vf.arbiter.core.database.model.location.CountyModel
import vf.arbiter.core.model.stored.location.County

/**
  * Used for accessing individual Countys
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object DbCounty extends SingleRowModelAccess[County] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CountyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CountyFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted County instance
	  * @return An access point to that County
	  */
	def apply(id: Int) = new DbSingleCounty(id)
	
	
	// NESTED	--------------------
	
	class DbSingleCounty(val id: Int) extends UniqueCountyAccess with UniqueModelAccess[County]
	{
		// IMPLEMENTED	--------------------
		
		override def condition = index <=> id
	}
}
