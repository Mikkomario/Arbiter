package vf.arbiter.gold.model.enumeration

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.model.template.ValueConvertible
import vf.arbiter.gold.controller.price.MetalPrices

/**
  * Represents a type of valuable metal used in trading
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
sealed trait Metal extends ValueConvertible
{
	// ABSTRACT	--------------------
	
	/**
	  * id used to represent this metal in database and json
	  */
	def id: Int
	/**
	 * @return A code used for this metal in the metal price API
	 */
	def code: String
	
	
	// IMPLEMENTED	--------------------
	
	override def toValue = id
	
	
	// OTHER    -----------------------
	
	/**
	 * @param currency Targeted monetary currency
	 * @return Access to this metal's prices in that currency
	 */
	def pricesIn(currency: Currency) = MetalPrices(this, currency)
}

object Metal
{
	// ATTRIBUTES	--------------------
	
	/**
	  * All available metal values
	  */
	val values: Vector[Metal] = Vector(Gold, Silver)
	
	
	// COMPUTED	--------------------
	
	/**
	  * The default metal (i.e. gold)
	  */
	def default = Gold
	
	
	// OTHER	--------------------
	
	/**
	  * @param id id representing a metal
	  * @return metal matching the specified id. None if the id didn't match any metal
	  */
	def findForId(id: Int) = values.find { _.id == id }
	
	/**
	  * @param id id matching a metal
	  * @return metal matching that id, or the default metal (gold)
	  */
	def forId(id: Int) = findForId(id).getOrElse(default)
	
	/**
	  * @param value A value representing an metal id
	  * @return metal matching the specified value, when the value is interpreted as an metal id, 
	  * or the default metal (gold)
	  */
	def fromValue(value: Value) = forId(value.getInt)
	
	
	// NESTED	--------------------
	
	case object Gold extends Metal
	{
		// ATTRIBUTES	--------------------
		
		override val id = 1
		override val code: String = "XAU"
	}
	
	case object Silver extends Metal
	{
		// ATTRIBUTES	--------------------
		
		override val id = 2
		override val code: String = "XAG"
	}
}

