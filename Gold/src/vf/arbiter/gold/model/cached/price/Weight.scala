package vf.arbiter.gold.model.cached.price

import utopia.flow.operator.numeric.DoubleLike
import utopia.flow.operator.sign.Sign
import vf.arbiter.gold.model.enumeration.WeightUnit
import vf.arbiter.gold.model.enumeration.WeightUnit.{Gram, Kilogram, TroyOunce}

object Weight
{
	/**
	 * 0g weight
	 */
	val zero = apply(0.0, Gram)
}

/**
 * Represents an amount of weight in some unit
 * @author Mikko Hilpinen
 * @since 14.9.2023, v1.4
 *
 * @constructor Constructs a new amount of weight
 * @param value Wrapped double value
 * @param unit Unit in which the 'value' is given
 */
case class Weight(value: Double, unit: WeightUnit) extends DoubleLike[Weight]
{
	// ATTRIBUTES   ---------------------
	
	override lazy val sign = Sign.of(value)
	
	
	// COMPUTED -------------------------
	
	/**
	 * @return This weight in grams
	 */
	def grams = in(Gram)
	/**
	 * @return This weight in kilograms
	 */
	def kilograms = in(Kilogram)
	/**
	 * @return This weight in troy ounces
	 */
	def troyOunces = in(TroyOunce)
	
	
	// IMPLEMENTED  ---------------------
	
	override def self = this
	override def zero = Weight.zero
	
	override def length = grams
	
	override def toString = s"$value $unit"
	
	override def compareTo(o: Weight) = value.compareTo(o.in(unit))
	
	override def +(other: Weight) = copy(value + other.in(unit))
	override def *(mod: Double) = copy(value * mod)
	
	
	// OTHER    -------------------------
	
	/**
	 * @param unit Targeted unit
	 * @return This weight amount in that unit
	 */
	def in(unit: WeightUnit) = value * unit.conversionModifierFrom(this.unit)
	
	/**
	 * @param unit Targeted unit
	 * @return This weight with the specified unit, with the value adjusted accordingly
	 */
	def to(unit: WeightUnit) = if (unit == this.unit) this else Weight(in(unit), unit)
}
