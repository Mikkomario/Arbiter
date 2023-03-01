package vf.arbiter.core.database.model.location

import java.time.Instant
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.location.CountyFactory
import vf.arbiter.core.model.partial.location.CountyData
import vf.arbiter.core.model.stored.location.County

/**
  * Used for constructing CountyModel instances and for inserting Countys to the database
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object CountyModel extends DataInserter[CountyModel, County, CountyData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains County name
	  */
	val nameAttName = "name"
	
	/**
	  * Name of the property that contains County creatorId
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains County created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains County name
	  */
	def nameColumn = table(nameAttName)
	
	/**
	  * Column that contains County creatorId
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains County created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = CountyFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: CountyData) = apply(None, Some(data.name), data.creatorId, Some(data.created))
	
	override def complete(id: Value, data: CountyData) = County(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this County was first created
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who registered this county
	  * @return A model containing only the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param id A County id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param name County name, with that county's or country's primary language
	  * @return A model containing only the specified name
	  */
	def withName(name: String) = apply(name = Some(name))
}

/**
  * Used for interacting with Counties in the database
  * @param id County database id
  * @param name County name, with that county's or country's primary language
  * @param creatorId Id of the user who registered this county
  * @param created Time when this County was first created
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class CountyModel(id: Option[Int] = None, name: Option[String] = None, creatorId: Option[Int] = None, 
	created: Option[Instant] = None) 
	extends StorableWithFactory[County]
{
	// IMPLEMENTED	--------------------
	
	override def factory = CountyModel.factory
	
	override def valueProperties = 
	{
		import CountyModel._
		Vector("id" -> id, nameAttName -> name, creatorIdAttName -> creatorId, createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created A new created
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param creatorId A new creatorId
	  * @return A new copy of this model with the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = copy(creatorId = Some(creatorId))
	
	/**
	  * @param name A new name
	  * @return A new copy of this model with the specified name
	  */
	def withName(name: String) = copy(name = Some(name))
}

