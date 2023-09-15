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
	 * @return Abbreviation used for this unit
	 */
	def abbreviation: String
	
	/**
	 * @param other Another unit of weight
	 * @return A modifier that must be applied to a value given in the 'other' unit when converting it to this unit
	 */
	def conversionModifierFrom(other: WeightUnit): Double
	
	
	// IMPLEMENTED	--------------------
	
	override def toValue = id
	
	override def toString = abbreviation
	
	
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
	 * @param str A string
	 * @return Weight unit represented by that string, if found. None otherwise.
	 */
	def findForString(str: String) = values.find { _.abbreviation ~== str }
	
	/**
	  * @param id id matching a weight unit
	  * @return weight unit matching that id. Failure if no matching value was found.
	  */
	def forId(id: Int) = 
		findForId(id).toTry { new NoSuchElementException(s"No value of WeightUnit matches id '$id'") }
	/**
	 * @param str A string
	 * @return A weight unit matching that string. Failure if no matching unit was found.
	 */
	def forString(str: String) =
		findForString(str).toTry { new NoSuchElementException(s"No value of WeightUnit matches '$str'") }
	
	/**
	  * @param value A value representing an weight unit id or abbreviation (String)
	  * @return weight unit matching the specified value, 
	  * when the value is interpreted as an weight unit id or abbreviation.
	 * Failure if no matching value was found.
	  */
	def fromValue(value: Value) = value.int match {
		case Some(id) => forId(id)
		case None => forString(value.getString)
	}
	
	
	// NESTED	--------------------
	
	/**
	  * 1000th of a kilogram. Used in the metric system.
	  * @since 14.09.2023
	  */
	case object Gram extends WeightUnit
	{
		// ATTRIBUTES	--------------------
		
		override val id = 1
		override val abbreviation: String = "g"
		
		override def conversionModifierFrom(other: WeightUnit): Double = other match {
			case Gram => 1.0
			case Kilogram => 1000
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
		override val abbreviation: String = "Kg"
		
		override def conversionModifierFrom(other: WeightUnit): Double = other match {
			case Kilogram => 1.0
			case Gram => 0.001
			case o => Gram.conversionModifierFrom(o) * 0.001
		}
	}
	
	case object TroyOunce extends WeightUnit
	{
		// ATTRIBUTES	--------------------
		
		override val id = 3
		override val abbreviation: String = "oz"
		
		override def conversionModifierFrom(other: WeightUnit): Double = other match {
			case TroyOunce => 1.0
			case Gram => 0.03215075
			case o => Gram.conversionModifierFrom(o) * 0.03215075
		}
	}
}

