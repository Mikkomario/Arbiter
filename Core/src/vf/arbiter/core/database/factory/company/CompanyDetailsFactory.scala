package vf.arbiter.core.database.factory.company

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.database.model.company.CompanyDetailsModel
import vf.arbiter.core.model.partial.company.CompanyDetailsData
import vf.arbiter.core.model.stored.company.CompanyDetails

/**
  * Used for reading CompanyDetails data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object CompanyDetailsFactory extends FromValidatedRowModelFactory[CompanyDetails] with Deprecatable
{
	// IMPLEMENTED	--------------------
	
	override def nonDeprecatedCondition = CompanyDetailsModel.nonDeprecatedCondition
	
	override def defaultOrdering = None
	
	override def table = CoreTables.companyDetails
	
	override def fromValidatedModel(valid: Model) =
		CompanyDetails(valid("id").getInt, CompanyDetailsData(valid("companyId").getInt, 
			valid("name").getString, valid("addressId").getInt, valid("taxCode").string, 
			valid("creatorId").int, valid("created").getInstant, valid("deprecatedAfter").instant, 
			valid("isOfficial").getBoolean))
}

