package vf.arbiter.core.database.access.single.company

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.core.database.factory.company.OrganizationCompanyFactory
import vf.arbiter.core.database.model.company.OrganizationCompanyModel
import vf.arbiter.core.model.stored.company.OrganizationCompany

/**
  * A common trait for access points that return individual and distinct OrganizationCompanys.
  * @author Mikko Hilpinen
  * @since 2021-10-10
  */
trait UniqueOrganizationCompanyAccess 
	extends SingleRowModelAccess[OrganizationCompany] 
		with DistinctModelAccess[OrganizationCompany, Option[OrganizationCompany], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the owner organization. None if no instance (or value) was found.
	  */
	def organizationId(implicit connection: Connection) = pullColumn(model.organizationIdColumn).int
	
	/**
	  * Id of the owned company. None if no instance (or value) was found.
	  */
	def companyId(implicit connection: Connection) = pullColumn(model.companyIdColumn).int
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = OrganizationCompanyModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = OrganizationCompanyFactory
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the companyId of the targeted OrganizationCompany instance(s)
	  * @param newCompanyId A new companyId to assign
	  * @return Whether any OrganizationCompany instance was affected
	  */
	def companyId_=(newCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.companyIdColumn, newCompanyId)
	
	/**
	  * Updates the organizationId of the targeted OrganizationCompany instance(s)
	  * @param newOrganizationId A new organizationId to assign
	  * @return Whether any OrganizationCompany instance was affected
	  */
	def organizationId_=(newOrganizationId: Int)(implicit connection: Connection) = 
		putColumn(model.organizationIdColumn, newOrganizationId)
}

