package vf.arbiter.command.model

/**
 * Used for storing selected language information
 * @author Mikko Hilpinen
 * @since 15.10.2021, v0.2
 */
case class SelectedLanguage(id: Int, name: String)
{
	override def toString = name
}
