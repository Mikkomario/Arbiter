package vf.arbiter.core.database.model.company

import java.time.Instant
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.arbiter.core.database.factory.company.CompanyFactory
import vf.arbiter.core.model.partial.company.CompanyData
import vf.arbiter.core.model.stored.company.Company

/**
  * Used for constructing CompanyModel instances and for inserting Companys to the database
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object CompanyModel extends DataInserter[CompanyModel, Company, CompanyData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains Company yCode
	  */
	val yCodeAttName = "yCode"
	
	/**
	  * Name of the property that contains Company creatorId
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains Company created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains Company yCode
	  */
	def yCodeColumn = table(yCodeAttName)
	
	/**
	  * Column that contains Company creatorId
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains Company created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = CompanyFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: CompanyData) = apply(None, Some(data.yCode), data.creatorId, Some(data.created))
	
	override def complete(id: Value, data: CompanyData) = Company(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this company was registered
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who registered this company to this system
	  * @return A model containing only the specified creatorId
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param id A Company id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param yCode Official registration code of this company (id in the country system)
	  * @return A model containing only the specified yCode
	  */
	def withYCode(yCode: String) = apply(yCode = Some(yCode))
}

/**
  * Used for interacting with Companies in the database
  * @param id Company database id
  * @param yCode Official registration code of this company (id in the country system)
  * @param creatorId Id of the user who registered this company to this system
  * @param created Time when this company was registered
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class CompanyModel(id: Option[Int] = None, yCode: Option[String] = None, creatorId: Option[Int] = None, 
	created: Option[Instant] = None) 
	extends StorableWithFactory[Company]
{
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyModel.factory
	
	override def valueProperties = 
	{
		import CompanyModel._
		Vector("id" -> id, yCodeAttName -> yCode, creatorIdAttName -> creatorId, createdAttName -> created)
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
	  * @param yCode A new yCode
	  * @return A new copy of this model with the specified yCode
	  */
	def withYCode(yCode: String) = copy(yCode = Some(yCode))
}

