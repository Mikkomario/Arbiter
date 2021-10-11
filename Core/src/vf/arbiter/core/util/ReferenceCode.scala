package vf.arbiter.core.util

import scala.util.{Failure, Success, Try}

/**
 * Utilities for dealing with reference codes
 * @author Mikko Hilpinen
 * @since 11.10.2021, v0.1
 */
object ReferenceCode
{
	/**
	 * Creates a new reference code
	 * @param firstPart The first number part in this code
	 * @param moreParts More number parts in this code
	 * @return A reference code based on the numbers
	 */
	def apply(firstPart: Int, moreParts: Int*) =
	{
		// Combines the parts together as a string
		val code = (firstPart +: moreParts).mkString
		// Calculates the check number
		val check = checkNumberFrom(code)
		// Combines the two together
		code + check
	}
	
	/**
	 * Tests whether the specified reference code is valid
	 * @param code A reference code
	 * @return Whether that reference code is valid
	 */
	def validate(code: String) =
	{
		if (code.isEmpty)
			false
		else
			Try { code.last.asDigit } match
			{
				case Success(checkNumber) => checkNumber == checkNumberFrom(code.dropRight(1))
				case Failure(_) => false
			}
	}
	
	/**
	 * @param code A reference code (without the check number)
	 * @return The check number that should be at the end of that code
	 */
	def checkNumberFrom(code: String) =
	{
		// Takes the digits from right to left and
		// multiplies them with a special multiplier that rotates between 7, 3 and 1
		val multiplierIterator = Iterator.continually(Vector(7, 3, 1)).flatten
		val sum = code.reverseIterator.flatMap { c => if (c.isDigit) Some(c.asDigit) else None }
			.foldLeft(0) { (sum, digit) => sum + digit * multiplierIterator.next() }
		// Finally, the sum is subtracted from the next number divisible by 10 (e.g. 120 - 112 = 8)
		// Exception: If the sum is divisible by 10, 0 is used
		val overflow = sum % 10
		if (overflow == 0) 0 else 10 - overflow
	}
}
