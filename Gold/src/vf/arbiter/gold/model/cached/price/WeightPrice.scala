package vf.arbiter.gold.model.cached.price

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.operator.numeric.DoubleLike
import utopia.flow.operator.sign.Sign
import utopia.flow.parse.string.Regex
import vf.arbiter.gold.model.enumeration.WeightUnit
import vf.arbiter.gold.model.enumeration.WeightUnit.{Gram, Kilogram, TroyOunce}

object WeightPrice
{
	// ATTRIBUTES   ---------------------------
	
	/**
	 * 0/g
	 */
	val zero = apply(0.0, Gram)
	
	
	// IMPLICIT -------------------------------
	
	implicit def numeric: Fractional[WeightPrice] = WeightPriceIsFractional
	
	
	// NESTED   -------------------------------
	
	object WeightPriceIsFractional extends Fractional[WeightPrice]
	{
		override def div(x: WeightPrice, y: WeightPrice): WeightPrice = WeightPrice(x / y, x.perUnit)
		override def plus(x: WeightPrice, y: WeightPrice): WeightPrice = x + y
		override def minus(x: WeightPrice, y: WeightPrice): WeightPrice = x - y
		override def times(x: WeightPrice, y: WeightPrice): WeightPrice = x * y.per(x.perUnit)
		
		override def negate(x: WeightPrice): WeightPrice = -x
		
		override def fromInt(x: Int): WeightPrice = WeightPrice(x, Gram)
		override def parseString(str: String): Option[WeightPrice] = {
			// Separates the number part from the unit part
			val parts = Regex.number.divide(str)
			parts.findMap { _.toOption }.flatMap { _.double }.map { price =>
				// Uses gram as the default unit
				val unit = parts
					.findMap { _.leftOption.flatMap { u => WeightUnit.findForString(u.trim) } }.getOrElse(Gram)
				WeightPrice(price, unit)
			}
		}
		
		override def toInt(x: WeightPrice): Int = x.perGram.toInt
		override def toLong(x: WeightPrice): Long = x.perGram.toLong
		override def toFloat(x: WeightPrice): Float = x.perGram.toFloat
		override def toDouble(x: WeightPrice): Double = x.perGram
		
		override def compare(x: WeightPrice, y: WeightPrice): Int = x.compareTo(y)
	}
}

/**
 * Represents a price given to a measure of weight of a precious metal
 * @author Mikko Hilpinen
 * @since 15.9.2023, v1.4
 *
 * @constructor Constructs a new price
 * @param price The price in appropriate currency
 * @param perUnit The unit of weight against which that price is calculated
 */
case class WeightPrice(price: Double, perUnit: WeightUnit) extends DoubleLike[WeightPrice]
{
	// ATTRIBUTES   -----------------------
	
	override lazy val sign = Sign.of(price)
	
	
	// COMPUTED ---------------------------
	
	/**
	 * @return Price per a gram of the appropriate metal
	 */
	def perGram = per(Gram)
	/**
	 * @return Price per a kilogram of the appropriate metal
	 */
	def perKilo = per(Kilogram)
	/**
	 * @return Price per a troy ounce of the appropriate metal
	 */
	def perTroyOunce = per(TroyOunce)
	
	
	// IMPLEMENTED  -----------------------
	
	override def self = this
	override def zero = WeightPrice.zero
	
	override def length = perGram
	
	override def compareTo(o: WeightPrice) = price.compareTo(o per perUnit)
	
	override def +(other: WeightPrice) = copy(price + other.per(perUnit))
	override def *(mod: Double) = copy(price * mod)
	
	
	// OTHER    --------------------------
	
	/**
	 * @param unit Targeted unit
	 * @return This price per single 'unit'
	 */
	def per(unit: WeightUnit) = price / unit.conversionModifierFrom(perUnit)
	
	/**
	 * @param money Some amount of money
	 * @return The amount of metal (weight) that may be purchased at this price using the specified amount of money.
	 *         Assumes that the specified amount is in the same currency as this price.
	 */
	def weightForMoney(money: Double) = Weight(money / price, perUnit)
	
	/**
	 * @param other Another price
	 * @return The ratio between these prices
	 */
	def /(other: WeightPrice): Double = price / (other per perUnit)
}