package vf.arbiter.core.database.factory.company

import utopia.flow.datastructure.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.company.OrganizationCompanyData
import vf.arbiter.core.model.stored.company.OrganizationCompany

/**
  * Used for reading OrganizationCompany data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
object OrganizationCompanyFactory extends FromValidatedRowModelFactory[OrganizationCompany]
{
	// IMPLEMENTED	--------------------
	
	override def table = CoreTables.organizationCompany
	
	override def defaultOrdering = None
	
	override def fromValidatedModel(valid: Model) =
		OrganizationCompany(valid("id").getInt, OrganizationCompanyData(valid("organizationId").getInt, 
			valid("companyId").getInt, valid("creatorId").int, valid("created").getInstant))
}

