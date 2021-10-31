package vf.arbiter.core.model.partial.company

import java.time.Instant
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now

/**
  * Connects organizations with their owned companies
  * @param organizationId Id of the owner organization
  * @param companyId Id of the owned company
  * @param creatorId Id of the user who created this link
  * @param created Time when this OrganizationCompany was first created
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class OrganizationCompanyData(organizationId: Int, companyId: Int, creatorId: Option[Int] = None, 
	created: Instant = Now) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("organization_id" -> organizationId, "company_id" -> companyId, 
			"creator_id" -> creatorId, "created" -> created))
}

