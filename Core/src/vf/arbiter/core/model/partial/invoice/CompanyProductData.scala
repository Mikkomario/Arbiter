package vf.arbiter.core.model.partial.invoice

import java.time.Instant
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now

/**
  * Represents a type of product sold by an individual company
  * @param companyId Id of the company that owns this product type
  * @param unitId Id representing the units in which this product or service is sold
  * @param defaultUnitPrice Default € price per single unit of this product
  * @param taxModifier A modifier that is applied to this product's price to get the applied tax
  * @param created Time when this product was registered
  * @param discontinuedAfter Time when this product was discontinued (no longer sold)
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
case class CompanyProductData(companyId: Int, unitId: Int, defaultUnitPrice: Option[Double] = None, 
	taxModifier: Double = 0.24, created: Instant = Now, discontinuedAfter: Option[Instant] = None) 
	extends ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * Whether this CompanyProduct has already been deprecated
	  */
	def isDeprecated = discontinuedAfter.isDefined
	
	/**
	  * Whether this CompanyProduct is still valid (not deprecated)
	  */
	def isValid = !isDeprecated
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("company_id" -> companyId, "unit_id" -> unitId, 
			"default_unit_price" -> defaultUnitPrice, "tax_modifier" -> taxModifier, "created" -> created, 
			"discontinued_after" -> discontinuedAfter))
}

