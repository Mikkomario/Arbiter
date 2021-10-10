package vf.arbiter.core.database.model.location

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.location.CountyFactory
import vf.arbiter.core.model.partial.location.CountyData
import vf.arbiter.core.model.stored.location.County

/**
  * Used for constructing CountyModel instances and for inserting Countys to the database
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
object CountyModel extends DataInserter[CountyModel, County, CountyData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains County name
	  */
	val nameAttName = "name"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains County name
	  */
	def nameColumn = table(nameAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = CountyFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: CountyData) = apply(None, Some(data.name))
	
	override def complete(id: Value, data: CountyData) = County(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param id A County id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param name County name, with that county's or country's primary language
	  * @return A model containing only the specified name
	  */
	def withName(name: Int) = apply(name = Some(name))
}

/**
  * Used for interacting with Countys in the database
  * @param id County database id
  * @param name County name, with that county's or country's primary language
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class CountyModel(id: Option[Int] = None, name: Option[Int] = None) extends StorableWithFactory[County]
{
	// IMPLEMENTED	--------------------
	
	override def factory = CountyModel.factory
	
	override def valueProperties = 
	{
		import CountyModel._
		Vector("id" -> id, nameAttName -> name)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param name A new name
	  * @return A new copy of this model with the specified name
	  */
	def withName(name: Int) = copy(name = Some(name))
}

