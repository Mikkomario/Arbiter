package vf.arbiter.core.database.factory.company

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.database.model.company.CompanyProductModel
import vf.arbiter.core.model.partial.company.CompanyProductData
import vf.arbiter.core.model.stored.company.CompanyProduct

/**
  * Used for reading CompanyProduct data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object CompanyProductFactory 
	extends FromValidatedRowModelFactory[CompanyProduct] with FromRowFactoryWithTimestamps[CompanyProduct] 
		with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def creationTimePropertyName = "created"
	
	override def nonDeprecatedCondition = CompanyProductModel.nonDeprecatedCondition
	
	override def table = CoreTables.companyProduct
	
	override def fromValidatedModel(valid: Model[Constant]) = 
		CompanyProduct(valid("id").getInt, CompanyProductData(valid("companyId").getInt, 
			valid("unitId").getInt, valid("defaultUnitPrice").double, valid("taxModifier").getDouble, 
			valid("creatorId").int, valid("created").getInstant, valid("discontinuedAfter").instant))
}

