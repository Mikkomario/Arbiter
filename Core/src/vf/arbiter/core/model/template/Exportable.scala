package vf.arbiter.core.model.template

import utopia.flow.datastructure.immutable.{Constant, Model}

/**
 * Common trait for models which can be exported in json format and transferred across database versions
 * @author Mikko Hilpinen
 * @since 29.10.2021, v1.1
 */
trait Exportable
{
	/**
	 * @return A model containing exportable data from this item
	 */
	def toExportModel: Model[Constant]
}
