package vf.arbiter.gold.model.enumeration

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.model.mutable.DataType.{IntType, StringType}
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
	val values: Pair[Currency] = Pair(Euro, Usd)
	
	
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
	 * @param value A value that represents a currency id, code or symbol
	 * @return Currency which matches the specified value. None if the specified value didn't match any currency.
	 */
	def findForValue(value: Value) = value.castTo(IntType, StringType) match {
		case Left(intVal) => intVal.int.flatMap(findForId)
		case Right(strVal) =>
			strVal.string.flatMap { str => values.find { c => (c.code ~== str) || (c.toString ~== str) } }
	}
	/**
	  * @param value A value that represents a currency id, code or symbol
	  * @return currency matching the specified value.
	 *         If the value didn't match any registered currency, returns the default currency (Euro).
	  */
	def fromValue(value: Value) = findForValue(value).getOrElse(default)
	
	
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
		
		override def toString = "€"
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
		
		override def toString = "$"
	}
}

