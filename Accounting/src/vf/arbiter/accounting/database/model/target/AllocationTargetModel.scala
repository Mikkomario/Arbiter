package vf.arbiter.accounting.database.model.target

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import utopia.vault.nosql.storable.deprecation.DeprecatableAfter
import vf.arbiter.accounting.database.factory.target.AllocationTargetFactory
import vf.arbiter.accounting.model.partial.target.AllocationTargetData
import vf.arbiter.accounting.model.stored.target.AllocationTarget

import java.time.Instant

/**
  * Used for constructing AllocationTargetModel instances and for inserting allocation targets to the database
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
object AllocationTargetModel 
	extends DataInserter[AllocationTargetModel, AllocationTarget, AllocationTargetData] 
		with DeprecatableAfter[AllocationTargetModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains allocation target company id
	  */
	val companyIdAttName = "companyId"
	
	/**
	  * Name of the property that contains allocation target capital ratio
	  */
	val capitalRatioAttName = "capitalRatio"
	
	/**
	  * Name of the property that contains allocation target applied since
	  */
	val appliedSinceAttName = "appliedSince"
	
	/**
	  * Name of the property that contains allocation target applied until
	  */
	val appliedUntilAttName = "appliedUntil"
	
	/**
	  * Name of the property that contains allocation target creator id
	  */
	val creatorIdAttName = "creatorId"
	
	/**
	  * Name of the property that contains allocation target created
	  */
	val createdAttName = "created"
	
	/**
	  * Name of the property that contains allocation target deprecated after
	  */
	val deprecatedAfterAttName = "deprecatedAfter"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains allocation target company id
	  */
	def companyIdColumn = table(companyIdAttName)
	
	/**
	  * Column that contains allocation target capital ratio
	  */
	def capitalRatioColumn = table(capitalRatioAttName)
	
	/**
	  * Column that contains allocation target applied since
	  */
	def appliedSinceColumn = table(appliedSinceAttName)
	
	/**
	  * Column that contains allocation target applied until
	  */
	def appliedUntilColumn = table(appliedUntilAttName)
	
	/**
	  * Column that contains allocation target creator id
	  */
	def creatorIdColumn = table(creatorIdAttName)
	
	/**
	  * Column that contains allocation target created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains allocation target deprecated after
	  */
	def deprecatedAfterColumn = table(deprecatedAfterAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = AllocationTargetFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: AllocationTargetData) = 
		apply(None, Some(data.companyId), Some(data.capitalRatio), Some(data.appliedSince), 
			data.appliedUntil, data.creatorId, Some(data.created), data.deprecatedAfter)
	
	override protected def complete(id: Value, data: AllocationTargetData) = AllocationTarget(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param appliedSince The first time from which this target is applied
	  * @return A model containing only the specified applied since
	  */
	def withAppliedSince(appliedSince: Instant) = apply(appliedSince = Some(appliedSince))
	
	/**
	  * 
		@param appliedUntil Time until which this target was applied. None if applied indefinitely (or until changed).
	  * @return A model containing only the specified applied until
	  */
	def withAppliedUntil(appliedUntil: Instant) = apply(appliedUntil = Some(appliedUntil))
	
	/**
	  * @param capitalRatio The targeted (minimum) capital ratio of the income (possibly after certain expenses) [0,
		
	  * 1].
	  * @return A model containing only the specified capital ratio
	  */
	def withCapitalRatio(capitalRatio: Double) = apply(capitalRatio = Some(capitalRatio))
	
	/**
	  * @param companyId Id of the company for which these targets apply
	  * @return A model containing only the specified company id
	  */
	def withCompanyId(companyId: Int) = apply(companyId = Some(companyId))
	
	/**
	  * @param created Time when this target was specified
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who specified these targets
	  * @return A model containing only the specified creator id
	  */
	def withCreatorId(creatorId: Int) = apply(creatorId = Some(creatorId))
	
	/**
	  * @param deprecatedAfter Time when this target was cancelled / deprecated. Deprecated targets don't apply, 
		
	  * not even in post.
	  * @return A model containing only the specified deprecated after
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = apply(deprecatedAfter = Some(deprecatedAfter))
	
	/**
	  * @param id A allocation target id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
}

/**
  * Used for interacting with AllocationTargets in the database
  * @param id allocation target database id
  * @author Mikko Hilpinen
  * @since 04.01.2024, v1.5
  */
case class AllocationTargetModel(id: Option[Int] = None, companyId: Option[Int] = None, 
	capitalRatio: Option[Double] = None, appliedSince: Option[Instant] = None, 
	appliedUntil: Option[Instant] = None, creatorId: Option[Int] = None, created: Option[Instant] = None, 
	deprecatedAfter: Option[Instant] = None) 
	extends StorableWithFactory[AllocationTarget]
{
	// IMPLEMENTED	--------------------
	
	override def factory = AllocationTargetModel.factory
	
	override def valueProperties = {
		import AllocationTargetModel._
		Vector("id" -> id, companyIdAttName -> companyId, capitalRatioAttName -> capitalRatio, 
			appliedSinceAttName -> appliedSince, appliedUntilAttName -> appliedUntil, 
			creatorIdAttName -> creatorId, createdAttName -> created, 
			deprecatedAfterAttName -> deprecatedAfter)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param appliedSince The first time from which this target is applied
	  * @return A new copy of this model with the specified applied since
	  */
	def withAppliedSince(appliedSince: Instant) = copy(appliedSince = Some(appliedSince))
	
	/**
	  * 
		@param appliedUntil Time until which this target was applied. None if applied indefinitely (or until changed).
	  * @return A new copy of this model with the specified applied until
	  */
	def withAppliedUntil(appliedUntil: Instant) = copy(appliedUntil = Some(appliedUntil))
	
	/**
	  * @param capitalRatio The targeted (minimum) capital ratio of the income (possibly after certain expenses) [0,
		
	  * 1].
	  * @return A new copy of this model with the specified capital ratio
	  */
	def withCapitalRatio(capitalRatio: Double) = copy(capitalRatio = Some(capitalRatio))
	
	/**
	  * @param companyId Id of the company for which these targets apply
	  * @return A new copy of this model with the specified company id
	  */
	def withCompanyId(companyId: Int) = copy(companyId = Some(companyId))
	
	/**
	  * @param created Time when this target was specified
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param creatorId Id of the user who specified these targets
	  * @return A new copy of this model with the specified creator id
	  */
	def withCreatorId(creatorId: Int) = copy(creatorId = Some(creatorId))
	
	/**
	  * @param deprecatedAfter Time when this target was cancelled / deprecated. Deprecated targets don't apply, 
		
	  * not even in post.
	  * @return A new copy of this model with the specified deprecated after
	  */
	def withDeprecatedAfter(deprecatedAfter: Instant) = copy(deprecatedAfter = Some(deprecatedAfter))
}

