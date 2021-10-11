package vf.arbiter.core.database.model.invoice

import java.time.Instant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import utopia.vault.nosql.storable.deprecation.NullDeprecatable
import vf.arbiter.core.database.factory.invoice.CompanyProductFactory
import vf.arbiter.core.model.partial.invoice.CompanyProductData
import vf.arbiter.core.model.stored.invoice.CompanyProduct

/**
  * Used for constructing CompanyProductModel instances and for inserting CompanyProducts to the database
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
object CompanyProductModel 
	extends DataInserter[CompanyProductModel, CompanyProduct, CompanyProductData] 
		with NullDeprecatable[CompanyProductModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains CompanyProduct companyId
	  */
	val companyIdAttName = "companyId"
	
	/**
	  * Name of the property that contains CompanyProduct unitId
	  */
	val unitIdAttName = "unitId"
	
	/**
	  * Name of the property that contains CompanyProduct defaultUnitPrice
	  */
	val defaultUnitPriceAttName = "defaultUnitPrice"
	
	/**
	  * Name of the property that contains CompanyProduct taxModifier
	  */
	val taxModifierAttName = "taxModifier"
	
	/**
	  * Name of the property that contains CompanyProduct created
	  */
	val createdAttName = "created"
	
	/**
	  * Name of the property that contains CompanyProduct discontinuedAfter
	  */
	val discontinuedAfterAttName = "discontinuedAfter"
	
	override val deprecationAttName = "discontinuedAfter"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains CompanyProduct companyId
	  */
	def companyIdColumn = table(companyIdAttName)
	
	/**
	  * Column that contains CompanyProduct unitId
	  */
	def unitIdColumn = table(unitIdAttName)
	
	/**
	  * Column that contains CompanyProduct defaultUnitPrice
	  */
	def defaultUnitPriceColumn = table(defaultUnitPriceAttName)
	
	/**
	  * Column that contains CompanyProduct taxModifier
	  */
	def taxModifierColumn = table(taxModifierAttName)
	
	/**
	  * Column that contains CompanyProduct created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains CompanyProduct discontinuedAfter
	  */
	def discontinuedAfterColumn = table(discontinuedAfterAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = CompanyProductFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: CompanyProductData) = 
		apply(None, Some(data.companyId), Some(data.unitId), data.defaultUnitPrice, Some(data.taxModifier), 
			Some(data.created), data.discontinuedAfter)
	
	override def complete(id: Value, data: CompanyProductData) = CompanyProduct(id.getInt, data)
	
	override def withDeprecatedAfter(deprecationTime: Instant) = withDiscontinuedAfter(deprecationTime)
	
	
	// OTHER	--------------------
	
	/**
	  * @param companyId Id of the company that owns this product type
	  * @return A model containing only the specified companyId
	  */
	def withCompanyId(companyId: Int) = apply(companyId = Some(companyId))
	
	/**
	  * @param created Time when this product was registered
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param defaultUnitPrice Default € price per single unit of this product
	  * @return A model containing only the specified defaultUnitPrice
	  */
	def withDefaultUnitPrice(defaultUnitPrice: Double) = apply(defaultUnitPrice = Some(defaultUnitPrice))
	
	/**
	  * @param discontinuedAfter Time when this product was discontinued (no longer sold)
	  * @return A model containing only the specified discontinuedAfter
	  */
	def withDiscontinuedAfter(discontinuedAfter: Instant) = apply(discontinuedAfter = Some(discontinuedAfter))
	
	/**
	  * @param id A CompanyProduct id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param taxModifier A modifier that is applied to this product's price to get the applied tax
	  * @return A model containing only the specified taxModifier
	  */
	def withTaxModifier(taxModifier: Double) = apply(taxModifier = Some(taxModifier))
	
	/**
	  * @param unitId Id representing the units in which this product or service is sold
	  * @return A model containing only the specified unitId
	  */
	def withUnitId(unitId: Int) = apply(unitId = Some(unitId))
}

/**
  * Used for interacting with CompanyProducts in the database
  * @param id CompanyProduct database id
  * @param companyId Id of the company that owns this product type
  * @param unitId Id representing the units in which this product or service is sold
  * @param defaultUnitPrice Default € price per single unit of this product
  * @param taxModifier A modifier that is applied to this product's price to get the applied tax
  * @param created Time when this product was registered
  * @param discontinuedAfter Time when this product was discontinued (no longer sold)
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
case class CompanyProductModel(id: Option[Int] = None, companyId: Option[Int] = None, 
	unitId: Option[Int] = None, defaultUnitPrice: Option[Double] = None, taxModifier: Option[Double] = None, 
	created: Option[Instant] = None, discontinuedAfter: Option[Instant] = None) 
	extends StorableWithFactory[CompanyProduct]
{
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyProductModel.factory
	
	override def valueProperties = 
	{
		import CompanyProductModel._
		Vector("id" -> id, companyIdAttName -> companyId, unitIdAttName -> unitId, 
			defaultUnitPriceAttName -> defaultUnitPrice, taxModifierAttName -> taxModifier, 
			createdAttName -> created, discontinuedAfterAttName -> discontinuedAfter)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param companyId A new companyId
	  * @return A new copy of this model with the specified companyId
	  */
	def withCompanyId(companyId: Int) = copy(companyId = Some(companyId))
	
	/**
	  * @param created A new created
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param defaultUnitPrice A new defaultUnitPrice
	  * @return A new copy of this model with the specified defaultUnitPrice
	  */
	def withDefaultUnitPrice(defaultUnitPrice: Double) = copy(defaultUnitPrice = Some(defaultUnitPrice))
	
	/**
	  * @param discontinuedAfter A new discontinuedAfter
	  * @return A new copy of this model with the specified discontinuedAfter
	  */
	def withDiscontinuedAfter(discontinuedAfter: Instant) = copy(discontinuedAfter = Some(discontinuedAfter))
	
	/**
	  * @param taxModifier A new taxModifier
	  * @return A new copy of this model with the specified taxModifier
	  */
	def withTaxModifier(taxModifier: Double) = copy(taxModifier = Some(taxModifier))
	
	/**
	  * @param unitId A new unitId
	  * @return A new copy of this model with the specified unitId
	  */
	def withUnitId(unitId: Int) = copy(unitId = Some(unitId))
}

