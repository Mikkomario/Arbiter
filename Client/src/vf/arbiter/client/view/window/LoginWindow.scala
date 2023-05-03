package vf.arbiter.client.view.window

import utopia.firmament.localization.LocalizedString
import utopia.firmament.model.{RowGroups, WindowButtonBlueprint}
import utopia.flow.generic.model.immutable.Model
import utopia.flow.view.immutable.View
import utopia.metropolis.model.combined.user.DetailedUser
import utopia.reach.component.template.ReachComponentLike
import utopia.reach.focus.FocusRequestable
import utopia.reach.window.InputRowBlueprint
import vf.arbiter.client.model.stored.auth.Session
import vf.arbiter.client.view.ViewSettings._

/**
 * Used for constructing login form windows
 * @author Mikko Hilpinen
 * @since 2.5.2023, v2.0
 */
class LoginWindow(users: Vector[DetailedUser]) extends ArbiterFormWindowFactory[Either[Boolean, DetailedUser], Any]
{
	override protected def title: LocalizedString = "Login"
	override protected def defaultResult = Left(false)
	
	override protected def inputTemplate: (Vector[RowGroups[InputRowBlueprint]], Any) = ???
	
	override protected def specifyButtons(context: Any, input: => Either[(String, ReachComponentLike with FocusRequestable), Model], warn: (String, LocalizedString) => Unit) = ???
}
