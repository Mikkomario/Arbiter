package vf.arbiter.core.database.access.single.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.company.CompanyProductFactory
import vf.arbiter.core.database.model.company.CompanyProductModel
import vf.arbiter.core.model.stored.company.CompanyProduct

/**
  * A common trait for access points that return individual and distinct CompanyProducts.
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
trait UniqueCompanyProductAccess 
	extends SingleRowModelAccess[CompanyProduct] 
		with DistinctModelAccess[CompanyProduct, Option[CompanyProduct], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the company that owns this product type. None if no instance (or value) was found.
	  */
	def companyId(implicit connection: Connection) = pullColumn(model.companyIdColumn).int
	
	/**
	  * Id representing the units in which this product or service is sold. None if no instance (or value) was found.
	  */
	def unitId(implicit connection: Connection) = pullColumn(model.unitIdColumn).int
	
	/**
	  * Default € price per single unit of this product. None if no instance (or value) was found.
	  */
	def defaultUnitPrice(implicit connection: Connection) = pullColumn(model.defaultUnitPriceColumn).double
	
	/**
	  * A modifier that is applied to this product's price to get the applied tax. None if no instance (or value) was found.
	  */
	def taxModifier(implicit connection: Connection) = pullColumn(model.taxModifierColumn).double
	
	/**
	  * Id of the user linked with this CompanyProduct. None if no instance (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this product was registered. None if no instance (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Time when this product was discontinued (no longer sold). None if no instance (or value) was found.
	  */
	def discontinuedAfter(implicit connection: Connection) = pullColumn(model.discontinuedAfterColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyProductModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyProductFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the companyId of the targeted CompanyProduct instance(s)
	  * @param newCompanyId A new companyId to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def companyId_=(newCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.companyIdColumn, newCompanyId)
	
	/**
	  * Updates the created of the targeted CompanyProduct instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted CompanyProduct instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the defaultUnitPrice of the targeted CompanyProduct instance(s)
	  * @param newDefaultUnitPrice A new defaultUnitPrice to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def defaultUnitPrice_=(newDefaultUnitPrice: Double)(implicit connection: Connection) = 
		putColumn(model.defaultUnitPriceColumn, newDefaultUnitPrice)
	
	/**
	  * Updates the discontinuedAfter of the targeted CompanyProduct instance(s)
	  * @param newDiscontinuedAfter A new discontinuedAfter to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def discontinuedAfter_=(newDiscontinuedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.discontinuedAfterColumn, newDiscontinuedAfter)
	
	/**
	  * Updates the taxModifier of the targeted CompanyProduct instance(s)
	  * @param newTaxModifier A new taxModifier to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def taxModifier_=(newTaxModifier: Double)(implicit connection: Connection) = 
		putColumn(model.taxModifierColumn, newTaxModifier)
	
	/**
	  * Updates the unitId of the targeted CompanyProduct instance(s)
	  * @param newUnitId A new unitId to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def unitId_=(newUnitId: Int)(implicit connection: Connection) = putColumn(model.unitIdColumn, newUnitId)
}

