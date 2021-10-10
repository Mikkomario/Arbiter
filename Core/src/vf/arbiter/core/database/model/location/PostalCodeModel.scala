package vf.arbiter.core.database.model.location

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
  * @since 2021-10-10
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
	  * The factory object used by this model type
	  */
	def factory = PostalCodeFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: PostalCodeData) = apply(None, Some(data.number), Some(data.countyId))
	
	override def complete(id: Value, data: PostalCodeData) = PostalCode(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param countyId Id of the county where this postal code is resides
	  * @return A model containing only the specified countyId
	  */
	def withCountyId(countyId: Int) = apply(countyId = Some(countyId))
	
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
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class PostalCodeModel(id: Option[Int] = None, number: Option[String] = None, 
	countyId: Option[Int] = None) 
	extends StorableWithFactory[PostalCode]
{
	// IMPLEMENTED	--------------------
	
	override def factory = PostalCodeModel.factory
	
	override def valueProperties = 
	{
		import PostalCodeModel._
		Vector("id" -> id, numberAttName -> number, countyIdAttName -> countyId)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param countyId A new countyId
	  * @return A new copy of this model with the specified countyId
	  */
	def withCountyId(countyId: Int) = copy(countyId = Some(countyId))
	
	/**
	  * @param number A new number
	  * @return A new copy of this model with the specified number
	  */
	def withNumber(number: String) = copy(number = Some(number))
}

