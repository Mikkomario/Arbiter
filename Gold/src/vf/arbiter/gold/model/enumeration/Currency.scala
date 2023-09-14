package vf.arbiter.gold.model.enumeration

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.model.template.ValueConvertible

/**
  * Represents a monetary currency used in trading
  * @author Mikko Hilpinen
  * @since 14.09.2023, v1.4
  */
sealed trait Currency extends ValueConvertible
{
	// ABSTRACT	--------------------
	
	/**
	  * id used to represent this currency in database and json
	  */
	def id: Int
	
	/**
	 * @return Code used for this currency
	 */
	def code: String
	
	
	// IMPLEMENTED	--------------------
	
	override def toValue = id
}

object Currency
{
	// ATTRIBUTES	--------------------
	
	/**
	  * All available currency values
	  */
	val values: Vector[Currency] = Vector(Euro, Usd)
	
	
	// COMPUTED	--------------------
	
	/**
	  * The default currency (i.e. euro)
	  */
	def default = Euro
	
	
	// OTHER	--------------------
	
	/**
	  * @param id id representing a currency
	  * @return currency matching the specified id. None if the id didn't match any currency
	  */
	def findForId(id: Int) = values.find { _.id == id }
	
	/**
	  * @param id id matching a currency
	  * @return currency matching that id, or the default currency (euro)
	  */
	def forId(id: Int) = findForId(id).getOrElse(default)
	
	/**
	  * @param value A value representing an currency id
	  * @return currency matching the specified value, when the value is interpreted as an currency id, 
	  * or the default currency (euro)
	  */
	def fromValue(value: Value) = forId(value.getInt)
	
	
	// NESTED	--------------------
	
	/**
	  * Euro; Used in the European Union
	  * @since 14.09.2023
	  */
	case object Euro extends Currency
	{
		// ATTRIBUTES	--------------------
		
		override val id = 1
		override val code: String = "EUR"
	}
	
	/**
	  * US dollar; Used in the United States and in other countries as well
	  * @since 14.09.2023
	  */
	case object Usd extends Currency
	{
		// ATTRIBUTES	--------------------
		
		override val id = 2
		override val code: String = "USD"
	}
}

