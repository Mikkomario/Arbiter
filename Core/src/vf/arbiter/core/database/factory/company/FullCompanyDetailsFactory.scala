package vf.arbiter.core.database.factory.company

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import vf.arbiter.core.database.factory.location.FullStreetAddressFactory
import vf.arbiter.core.model.combined.company.FullCompanyDetails
import vf.arbiter.core.model.combined.location.FullStreetAddress
import vf.arbiter.core.model.stored.company.CompanyDetails

/**
 * Used for reading company details, including full address information
 * @author Mikko Hilpinen
 * @since 18.10.2021, v0.2
 */
object FullCompanyDetailsFactory extends CombiningFactory[FullCompanyDetails, CompanyDetails, FullStreetAddress]
{
	override def parentFactory = CompanyDetailsFactory
	
	override def childFactory = FullStreetAddressFactory
	
	override def apply(parent: CompanyDetails, child: FullStreetAddress) = parent + child
}
