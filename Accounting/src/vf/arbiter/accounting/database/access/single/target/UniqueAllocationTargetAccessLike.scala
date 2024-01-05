package vf.arbiter.accounting.database.access.single.target

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.arbiter.accounting.database.model.target.AllocationTargetModel

import java.time.Instant

/**
  * A common trait for access points which target individual allocation targets or similar items at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait UniqueAllocationTargetAccessLike[+A] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the company for which these targets apply. None if no allocation target (or value) was found.
	  */
	def companyId(implicit connection: Connection) = pullColumn(model.companyIdColumn).int
	
	/**
	  * The targeted (minimum) capital ratio of the income (possibly after certain expenses) [0,
	  * 1].. None if no allocation target (or value) was found.
	  */
	def capitalRatio(implicit connection: Connection) = pullColumn(model.capitalRatioColumn).double
	
	/**
	  * The first time from which this target is applied. None if no allocation target (or value) was found.
	  */
	def appliedSince(implicit connection: Connection) = pullColumn(model.appliedSinceColumn).instant
	
	/**
	  * Time until which this target was applied. None if applied indefinitely (or until changed).. None if
	  *  no allocation target (or value) was found.
	  */
	def appliedUntil(implicit connection: Connection) = pullColumn(model.appliedUntilColumn).instant
	
	/**
	  * Id of the user who specified these targets. None if no allocation target (or value) was found.
	  */
	def creatorId(implicit connection: Connection) = pullColumn(model.creatorIdColumn).int
	
	/**
	  * Time when this target was specified. None if no allocation target (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Time when this target was cancelled / deprecated. Deprecated targets don't apply, 
	  * not even in post.. None if no allocation target (or value) was found.
	  */
	def deprecatedAfter(implicit connection: Connection) = pullColumn(model.deprecatedAfterColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AllocationTargetModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the first apply times of the targeted allocation targets
	  * @param newAppliedSince A new applied since to assign
	  * @return Whether any allocation target was affected
	  */
	def appliedSince_=(newAppliedSince: Instant)(implicit connection: Connection) = 
		putColumn(model.appliedSinceColumn, newAppliedSince)
	
	/**
	  * Updates the apply end times of the targeted allocation targets
	  * @param newAppliedUntil A new applied until to assign
	  * @return Whether any allocation target was affected
	  */
	def appliedUntil_=(newAppliedUntil: Instant)(implicit connection: Connection) = 
		putColumn(model.appliedUntilColumn, newAppliedUntil)
	
	/**
	  * Updates the capital ratios of the targeted allocation targets
	  * @param newCapitalRatio A new capital ratio to assign
	  * @return Whether any allocation target was affected
	  */
	def capitalRatio_=(newCapitalRatio: Double)(implicit connection: Connection) = 
		putColumn(model.capitalRatioColumn, newCapitalRatio)
	
	/**
	  * Updates the company ids of the targeted allocation targets
	  * @param newCompanyId A new company id to assign
	  * @return Whether any allocation target was affected
	  */
	def companyId_=(newCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.companyIdColumn, newCompanyId)
	
	/**
	  * Updates the creation times of the targeted allocation targets
	  * @param newCreated A new created to assign
	  * @return Whether any allocation target was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted allocation targets
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any allocation target was affected
	  */
	def creatorId_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the deprecation times of the targeted allocation targets
	  * @param newDeprecatedAfter A new deprecated after to assign
	  * @return Whether any allocation target was affected
	  */
	def deprecatedAfter_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.deprecatedAfterColumn, newDeprecatedAfter)
}

