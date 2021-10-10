package vf.arbiter.core.model.partial.company

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._

/**
  * Connects organizations with their owned companies
  * @param organizationId Id of the owner organization
  * @param companyId Id of the owned company
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
case class OrganizationCompanyData(organizationId: Int, companyId: Int) extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("organization_id" -> organizationId, "company_id" -> companyId))
}

