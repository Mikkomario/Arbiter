package vf.arbiter.core.test

import utopia.flow.util.StringExtensions._
import vf.arbiter.core.util.ReferenceCode

/**
 * Used for generating new reference codes
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
object ReferenceCodeApp extends App
{
	val parts = args.toVector.map { _.digits }.filter { _.nonEmpty }.map { _.toInt }
	if (parts.isEmpty)
		println("Please specify reference code parts")
	else
	{
		println(s"Code from: ${parts.mkString(" ")}")
		val code = ReferenceCode(parts.head, parts.tail: _*)
		println(code)
		assert(ReferenceCode.validate(code))
	}
}
