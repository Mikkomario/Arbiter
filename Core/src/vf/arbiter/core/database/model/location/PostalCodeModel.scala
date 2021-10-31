package vf.arbiter.core.database.model.location

import java.time.Instant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.location.PostalCodeFactory
import vf.arbiter.core.model.partial.location.PostalCodeData
import vf.arbiter.core.model.stored.location.PostalCode

/**
  * Used for constructing PostalCodeModel instances and for inserting PostalCodes to the database
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object PostalCodeModel extends DataInserter[PostalCodeModel, PostalCode, PostalCodeData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains PostalCode number
	  */
	val numberAttName = "number"
	
	/**
	  * Name of the property that contains PostalCode countyId
	  */
	val countyIdAttName = "countyId"
	
	/**
	  * Name of the property that contains PostalCode creatorId
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains PostalCode created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains PostalCode number
	  */
	def numberColumn = table(numberAttName)
	
	/**
	  * Column that contains PostalCode countyId
	  */
	def countyIdColumn = table(countyIdAttName)
	
	/**
	  * Column that contains PostalCode creatorId
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains PostalCode created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = PostalCodeFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: PostalCodeData) = 
		apply(None, Some(data.number), Some(data.countyId), data.creatorId, Some(data.created))
	
	override def complete(id: Value, data: PostalCodeData) = PostalCode(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param countyId Id of the county where this postal code is resides
	  * @return A model containing only the specified countyId
	  */
	def withCountyId(countyId: Int) = apply(countyId = Some(countyId))
	
	/**
	  * @param created Time when this PostalCode was first created
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user linked with this PostalCode
	  * @return A model containing only the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param id A PostalCode id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param number The number portion of this postal code
	  * @return A model containing only the specified number
	  */
	def withNumber(number: String) = apply(number = Some(number))
}

/**
  * Used for interacting with PostalCodes in the database
  * @param id PostalCode database id
  * @param number The number portion of this postal code
  * @param countyId Id of the county where this postal code is resides
  * @param creatorId Id of the user linked with this PostalCode
  * @param created Time when this PostalCode was first created
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class PostalCodeModel(id: Option[Int] = None, number: Option[String] = None, 
	countyId: Option[Int] = None, creatorId: Option[Int] = None, created: Option[Instant] = None) 
	extends StorableWithFactory[PostalCode]
{
	// IMPLEMENTED	--------------------
	
	override def factory = PostalCodeModel.factory
	
	override def valueProperties = 
	{
		import PostalCodeModel._
		Vector("id" -> id, numberAttName -> number, countyIdAttName -> countyId, 
			creatorIdAttName -> creatorId, createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param countyId A new countyId
	  * @return A new copy of this model with the specified countyId
	  */
	def withCountyId(countyId: Int) = copy(countyId = Some(countyId))
	
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
	  * @param number A new number
	  * @return A new copy of this model with the specified number
	  */
	def withNumber(number: String) = copy(number = Some(number))
}

