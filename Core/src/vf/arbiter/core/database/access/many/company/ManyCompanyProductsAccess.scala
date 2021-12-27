package vf.arbiter.core.database.access.many.company

import java.time.Instant
import utopia.citadel.database.access.many.description.ManyDescribedAccess
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.{FilterableView, SubView}
import utopia.vault.sql.Condition
import vf.arbiter.core.database.access.many.description.DbCompanyProductDescriptions
import vf.arbiter.core.database.factory.company.CompanyProductFactory
import vf.arbiter.core.database.model.company.CompanyProductModel
import vf.arbiter.core.model.combined.company.DescribedCompanyProduct
import vf.arbiter.core.model.stored.company.CompanyProduct

object ManyCompanyProductsAccess
{
	// NESTED	--------------------
	
	private class ManyCompanyProductsSubView(override val parent: ManyRowModelAccess[CompanyProduct], 
		override val filterCondition: Condition) 
		extends ManyCompanyProductsAccess with SubView
}

/**
  * A common trait for access points which target multiple CompanyProducts at a time
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
trait ManyCompanyProductsAccess 
	extends ManyRowModelAccess[CompanyProduct] 
		with ManyDescribedAccess[CompanyProduct, DescribedCompanyProduct]
		with FilterableView[ManyCompanyProductsAccess]
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
	  * creatorIds of the accessible CompanyProducts
	  */
	def creatorIds(implicit connection: Connection) = 
		pullColumn(model.creatorIdColumn).flatMap { value => value.int }
	
	/**
	  * creationTimes of the accessible CompanyProducts
	  */
	def creationTimes(implicit connection: Connection) = 
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
	
	override protected def describedFactory = DescribedCompanyProduct
	
	override protected def manyDescriptionsAccess = DbCompanyProductDescriptions
	
	override def filter(additionalCondition: Condition): ManyCompanyProductsAccess = 
		new ManyCompanyProductsAccess.ManyCompanyProductsSubView(this, additionalCondition)
	
	override def idOf(item: CompanyProduct) = item.id
	
	
	// OTHER	--------------------
	
	/**
	 * @param companyId Target company id
	 * @return An access point to that company's products
	 */
	def ofCompanyWithId(companyId: Int) = filter(model.withCompanyId(companyId).toCondition)
	
	/**
	  * Updates the companyId of the targeted CompanyProduct instance(s)
	  * @param newCompanyId A new companyId to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def companyIds_=(newCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.companyIdColumn, newCompanyId)
	
	/**
	  * Updates the created of the targeted CompanyProduct instance(s)
	  * @param newCreated A new created to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creatorId of the targeted CompanyProduct instance(s)
	  * @param newCreatorId A new creatorId to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def creatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the defaultUnitPrice of the targeted CompanyProduct instance(s)
	  * @param newDefaultUnitPrice A new defaultUnitPrice to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def defaultUnitPrices_=(newDefaultUnitPrice: Double)(implicit connection: Connection) = 
		putColumn(model.defaultUnitPriceColumn, newDefaultUnitPrice)
	
	/**
	  * Updates the discontinuedAfter of the targeted CompanyProduct instance(s)
	  * @param newDiscontinuedAfter A new discontinuedAfter to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def discontinuedAfters_=(newDiscontinuedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.discontinuedAfterColumn, newDiscontinuedAfter)
	
	/**
	  * Updates the taxModifier of the targeted CompanyProduct instance(s)
	  * @param newTaxModifier A new taxModifier to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def taxModifiers_=(newTaxModifier: Double)(implicit connection: Connection) = 
		putColumn(model.taxModifierColumn, newTaxModifier)
	
	/**
	  * Updates the unitId of the targeted CompanyProduct instance(s)
	  * @param newUnitId A new unitId to assign
	  * @return Whether any CompanyProduct instance was affected
	  */
	def unitIds_=(newUnitId: Int)(implicit connection: Connection) = putColumn(model.unitIdColumn, newUnitId)
}

