package vf.arbiter.core.database.factory.company

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.arbiter.core.database.CoreTables
import vf.arbiter.core.model.partial.company.OrganizationCompanyData
import vf.arbiter.core.model.stored.company.OrganizationCompany

/**
  * Used for reading OrganizationCompany data from the DB
  * @author Mikko Hilpinen
  * @since 2021-10-14
  */
object OrganizationCompanyFactory extends FromValidatedRowModelFactory[OrganizationCompany]
{
	// IMPLEMENTED	--------------------
	
	override def table = CoreTables.organizationCompany
	
	override def fromValidatedModel(valid: Model[Constant]) = 
		OrganizationCompany(valid("id").getInt, OrganizationCompanyData(valid("organizationId").getInt, 
			valid("companyId").getInt, valid("creatorId").int, valid("created").getInstant))
}

