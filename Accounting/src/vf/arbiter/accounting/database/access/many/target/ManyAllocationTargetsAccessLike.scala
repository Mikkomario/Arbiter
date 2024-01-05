package vf.arbiter.accounting.database.access.many.target

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.NullDeprecatableView
import vf.arbiter.accounting.database.model.target.AllocationTargetModel

import java.time.Instant

/**
  * A common trait for access points which target multiple allocation targets or similar instances at a time
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
trait ManyAllocationTargetsAccessLike[+A, +Repr] 
	extends ManyModelAccess[A] with Indexed with NullDeprecatableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * company ids of the accessible allocation targets
	  */
	def companyIds(implicit connection: Connection) = pullColumn(model.companyIdColumn).map { v => v.getInt }
	
	/**
	  * capital ratios of the accessible allocation targets
	  */
	def capitalRatios(implicit connection: Connection) = 
		pullColumn(model.capitalRatioColumn).map { v => v.getDouble }
	
	/**
	  * first apply times of the accessible allocation targets
	  */
	def firstApplyTimes(implicit connection: Connection) = 
		pullColumn(model.appliedSinceColumn).map { v => v.getInstant }
	
	/**
	  * apply end times of the accessible allocation targets
	  */
	def applyEndTimes(implicit connection: Connection) = 
		pullColumn(model.appliedUntilColumn).flatMap { v => v.instant }
	
	/**
	  * creator ids of the accessible allocation targets
	  */
	def creatorIds(implicit connection: Connection) = pullColumn(model.creatorIdColumn).flatMap { v => v.int }
	
	/**
	  * creation times of the accessible allocation targets
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	/**
	  * deprecation times of the accessible allocation targets
	  */
	def deprecationTimes(implicit connection: Connection) = 
		pullColumn(model.deprecatedAfterColumn).flatMap { v => v.instant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AllocationTargetModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the apply end times of the targeted allocation targets
	  * @param newAppliedUntil A new applied until to assign
	  * @return Whether any allocation target was affected
	  */
	def applyEndTimes_=(newAppliedUntil: Instant)(implicit connection: Connection) = 
		putColumn(model.appliedUntilColumn, newAppliedUntil)
	
	/**
	  * Updates the capital ratios of the targeted allocation targets
	  * @param newCapitalRatio A new capital ratio to assign
	  * @return Whether any allocation target was affected
	  */
	def capitalRatios_=(newCapitalRatio: Double)(implicit connection: Connection) = 
		putColumn(model.capitalRatioColumn, newCapitalRatio)
	
	/**
	  * Updates the company ids of the targeted allocation targets
	  * @param newCompanyId A new company id to assign
	  * @return Whether any allocation target was affected
	  */
	def companyIds_=(newCompanyId: Int)(implicit connection: Connection) = 
		putColumn(model.companyIdColumn, newCompanyId)
	
	/**
	  * Updates the creation times of the targeted allocation targets
	  * @param newCreated A new created to assign
	  * @return Whether any allocation target was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the creator ids of the targeted allocation targets
	  * @param newCreatorId A new creator id to assign
	  * @return Whether any allocation target was affected
	  */
	def creatorIds_=(newCreatorId: Int)(implicit connection: Connection) = 
		putColumn(model.creatorIdColumn, newCreatorId)
	
	/**
	  * Updates the deprecation times of the targeted allocation targets
	  * @param newDeprecatedAfter A new deprecated after to assign
	  * @return Whether any allocation target was affected
	  */
	def deprecationTimes_=(newDeprecatedAfter: Instant)(implicit connection: Connection) = 
		putColumn(model.deprecatedAfterColumn, newDeprecatedAfter)
	
	/**
	  * Updates the first apply times of the targeted allocation targets
	  * @param newAppliedSince A new applied since to assign
	  * @return Whether any allocation target was affected
	  */
	def firstApplyTimes_=(newAppliedSince: Instant)(implicit connection: Connection) = 
		putColumn(model.appliedSinceColumn, newAppliedSince)
}

