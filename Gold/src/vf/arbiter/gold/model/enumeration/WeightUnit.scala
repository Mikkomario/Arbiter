package vf.arbiter.gold.model.enumeration

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.model.template.ValueConvertible
import vf.arbiter.gold.model.cached.price.Weight

/**
  * Represents a unit used when measuring weights
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
sealed trait WeightUnit extends ValueConvertible
{
	// ABSTRACT	--------------------
	
	/**
	  * id used to represent this weight unit in database and json
	  */
	def id: Int
	
	/**
	 * @param other Another unit of weight
	 * @return A modifier that must be applied to a value given in the 'other' unit when converting it to this unit
	 */
	def conversionModifierFrom(other: WeightUnit): Double
	
	
	// IMPLEMENTED	--------------------
	
	override def toValue = id
	
	
	// OTHER    ------------------------
	
	/**
	 * @param amount Amount in this weight unit
	 * @return Specified amount of weight in this unit
	 */
	def apply(amount: Double) = Weight(amount, this)
}

object WeightUnit
{
	// ATTRIBUTES	--------------------
	
	/**
	  * All available weight unit values
	  */
	val values: Vector[WeightUnit] = Vector(Gram, Kilogram, TroyOunce)
	
	
	// OTHER	--------------------
	
	/**
	  * @param id id representing a weight unit
	  * @return weight unit matching the specified id. None if the id didn't match any weight unit
	  */
	def findForId(id: Int) = values.find { _.id == id }
	
	/**
	  * @param id id matching a weight unit
	  * @return weight unit matching that id. Failure if no matching value was found.
	  */
	def forId(id: Int) = 
		findForId(id).toTry { new NoSuchElementException(s"No value of WeightUnit matches id '$id'") }
	
	/**
	  * @param value A value representing an weight unit id
	  * @return weight unit matching the specified value, 
	  * when the value is interpreted as an weight unit id. Failure if no matching value was found.
	  */
	def fromValue(value: Value) = forId(value.getInt)
	
	
	// NESTED	--------------------
	
	/**
	  * 1000th of a kilogram. Used in the metric system.
	  * @since 14.09.2023
	  */
	case object Gram extends WeightUnit
	{
		// ATTRIBUTES	--------------------
		
		override val id = 1
		
		override def conversionModifierFrom(other: WeightUnit): Double = other match {
			case Gram => 1.0
			case Kilogram => 0.001
			case TroyOunce => 31.10348
		}
	}
	
	/**
	  * 1000 grams. Used in the metric system.
	  * @since 14.09.2023
	  */
	case object Kilogram extends WeightUnit
	{
		// ATTRIBUTES	--------------------
		
		override val id = 2
		
		override def conversionModifierFrom(other: WeightUnit): Double = other match {
			case Kilogram => 1.0
			case Gram => 1000
			case o => Gram.conversionModifierFrom(o) * 1000
		}
	}
	
	case object TroyOunce extends WeightUnit
	{
		// ATTRIBUTES	--------------------
		
		override val id = 3
		
		override def conversionModifierFrom(other: WeightUnit): Double = other match {
			case TroyOunce => 1.0
			case Gram => 0.03215075
			case o => Gram.conversionModifierFrom(o) * 0.03215075
		}
	}
}

