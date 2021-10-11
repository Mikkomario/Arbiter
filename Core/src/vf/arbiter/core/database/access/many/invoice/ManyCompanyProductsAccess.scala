package vf.arbiter.core.database.access.many.invoice

import java.time.Instant
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.invoice.CompanyProductFactory
import vf.arbiter.core.database.model.invoice.CompanyProductModel
import vf.arbiter.core.model.stored.invoice.CompanyProduct

/**
  * A common trait for access points which target multiple CompanyProducts at a time
  * @author Mikko Hilpinen
  * @since 2021-10-11
  */
trait ManyCompanyProductsAccess extends ManyRowModelAccess[CompanyProduct] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * companyIds of the accessible CompanyProducts
	  */
	def companyIds(implicit connection: Connection) = 
		pullColumn(model.companyIdColumn).flatMap { value => value.int }
	/**
	  * unitIds of the accessible CompanyProducts
	  */
	def unitIds(implicit connection: Connection) = pullColumn(model.unitIdColumn)
		.flatMap { value => value.int }
	/**
	  * defaultUnitPrices of the accessible CompanyProducts
	  */
	def defaultUnitPrices(implicit connection: Connection) = 
		pullColumn(model.defaultUnitPriceColumn).flatMap { value => value.double }
	/**
	  * taxModifiers of the accessible CompanyProducts
	  */
	def taxModifiers(implicit connection: Connection) = 
		pullColumn(model.taxModifierColumn).flatMap { value => value.double }
	/**
	  * createds of the accessible CompanyProducts
	  */
	def createds(implicit connection: Connection) = 
		pullColumn(model.createdColumn).flatMap { value => value.instant }
	/**
	  * discontinuedAfters of the accessible CompanyProducts
	  */
	def discontinuedAfters(implicit connection: Connection) = 
		pullColumn(model.discontinuedAfterColumn).flatMap { value => value.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).flatMap { id => id.int }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = CompanyProductModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = CompanyProductFactory
	
	override protected def defaultOrdering = Some(factory.defaultOrdering)
	
	
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

