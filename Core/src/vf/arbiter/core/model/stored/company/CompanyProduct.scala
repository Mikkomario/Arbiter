package vf.arbiter.core.model.stored.company

import utopia.flow.datastructure.immutable.Constant
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.template.StoredModelConvertible
import vf.arbiter.core.database.access.single.company.DbSingleCompanyProduct
import vf.arbiter.core.model.partial.company.CompanyProductData
import vf.arbiter.core.model.template.Exportable

/**
  * Represents a CompanyProduct that has already been stored in the database
  * @param id id of this CompanyProduct in the database
  * @param data Wrapped CompanyProduct data
  * @author Mikko Hilpinen
  * @since 2021-10-31
  */
case class CompanyProduct(id: Int, data: CompanyProductData) 
	extends StoredModelConvertible[CompanyProductData] with Exportable
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this CompanyProduct in the database
	  */
	def access = DbSingleCompanyProduct(id)
	
	
	// IMPLEMENTED  ----------------
	
	override def toExportModel = Constant("id", id) +: data.toExportModel
}

