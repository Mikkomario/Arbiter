package vf.arbiter.core.database.access.many.company

import utopia.vault.nosql.access.many.model.{ManyModelAccess, ManyRowModelAccess}
import utopia.vault.nosql.view.SubView
import utopia.vault.sql.Condition
import vf.arbiter.core.database.access.many.company.ManyFullCompanyDetailsAccess.ManyFullCompanyDetailsSubView
import vf.arbiter.core.database.factory.company.FullCompanyDetailsFactory
import vf.arbiter.core.model.combined.company.FullCompanyDetails

object ManyFullCompanyDetailsAccess
{
	private class ManyFullCompanyDetailsSubView(override val parent: ManyModelAccess[FullCompanyDetails],
	                                            override val filterCondition: Condition)
		extends ManyFullCompanyDetailsAccess with SubView
}

/**
 * Used for accessing multiple company details at a time, including full address information
 * @author Mikko Hilpinen
 * @since 5.5.2022, v1.3
 */
trait ManyFullCompanyDetailsAccess
	extends ManyCompanyDetailsAccessLike[FullCompanyDetails, ManyFullCompanyDetailsAccess]
		with ManyRowModelAccess[FullCompanyDetails]
{
	override def factory = FullCompanyDetailsFactory
	
	override def filter(additionalCondition: Condition): ManyFullCompanyDetailsAccess =
		new ManyFullCompanyDetailsSubView(this, additionalCondition)
}
