package vf.arbiter.gold.model.cached.price

import utopia.flow.operator.MayBeZero
import utopia.flow.operator.combine.{Combinable, LinearScalable}
import utopia.flow.operator.sign.{HasSign, Sign}
import utopia.flow.util.Mutate
import vf.arbiter.gold.model.enumeration.Currency

import scala.language.implicitConversions

object Price
{
	// IMPLICIT ---------------------------
	
	implicit def amountToPrice(amount: Double)(implicit currency: Currency): Price = apply(amount, currency)
}

/**
 * Represents a monetary amount specified in a (fiat) currency
 *
 * @author Mikko Hilpinen
 * @since 06.12.2024, v1.5.1
 */
case class Price(amount: Double, currency: Currency)
	extends LinearScalable[Price] with Combinable[Double, Price] with MayBeZero[Price] with HasSign
{
	// ATTRIBUTES   ------------------------
	
	override lazy val sign = Sign.of(amount)
	
	
	// IMPLEMENTED   -----------------------
	
	override def self = this
	
	override def zero = copy(amount = 0.0)
	override def isZero: Boolean = amount == 0.0
	
	override def toString = f"$amount%1.2f $currency"
	
	override def +(other: Double) = mapAmount { _ + other }
	override def *(mod: Double) = mapAmount { _ * mod }
	
	
	// OTHER    ----------------------------
	
	def mapAmount(f: Mutate[Double]) = copy(amount = f(amount))
}
