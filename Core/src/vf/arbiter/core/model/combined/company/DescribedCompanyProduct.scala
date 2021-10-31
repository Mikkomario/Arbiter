package vf.arbiter.core.model.combined.company

import utopia.metropolis.model.combined.description.{DescribedFactory, DescribedWrapper, LinkedDescription, SimplyDescribed}
import utopia.metropolis.model.stored.description.DescriptionRole
import vf.arbiter.core.model.combined.invoice.DescribedItemUnit
import vf.arbiter.core.model.stored.company.CompanyProduct

object DescribedCompanyProduct extends DescribedFactory[CompanyProduct, DescribedCompanyProduct]

/**
  * Combines CompanyProduct with the linked descriptions
  * @param companyProduct CompanyProduct to wrap
  * @param descriptions Descriptions concerning the wrapped CompanyProduct
  * @since 2021-10-31
  */
case class DescribedCompanyProduct(companyProduct: CompanyProduct, descriptions: Set[LinkedDescription]) 
	extends DescribedWrapper[CompanyProduct] with SimplyDescribed
{
	// IMPLEMENTED	--------------------
	
	override def wrapped = companyProduct
	
	override protected def simpleBaseModel(roles: Iterable[DescriptionRole]) = wrapped.toModel
	
	
	// OTHER    ------------------------
	
	/**
	 * @param unit Unit information
	 * @return This product with that information included
	 */
	def +(unit: DescribedItemUnit) = FullCompanyProduct(this, unit)
}

