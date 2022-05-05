package vf.arbiter.core.database.access.many.company

import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.view.{NonDeprecatedView, UnconditionalView}
import utopia.vault.sql.SqlExtensions._
import vf.arbiter.core.model.combined.company.FullCompanyDetails

/**
 * The root access point for accessing multiple full company details at a time
 * @author Mikko Hilpinen
 * @since 5.5.2022, v1.3
 */
object DbManyFullCompanyDetails extends ManyFullCompanyDetailsAccess with NonDeprecatedView[FullCompanyDetails]
{
	// COMPUTED ----------------------------
	
	/**
	 * @return A copy of this access point which includes historical records
	 */
	def includingHistory = DbAllFullCompanyDetails
	
	
	// OTHER    ----------------------------
	
	/**
	 * @param ids Ids of the targeted company details
	 * @return An access point to those details
	 */
	def apply(ids: Iterable[Int]) = new DbFullCompanyDetailsSubSet(ids)
	
	
	// NESTED   ----------------------------
	
	object DbAllFullCompanyDetails extends ManyFullCompanyDetailsAccess with UnconditionalView
	
	class DbFullCompanyDetailsSubSet(_ids: Iterable[Int]) extends ManyFullCompanyDetailsAccess
	{
		override def globalCondition = Some(index in _ids)
	}
}