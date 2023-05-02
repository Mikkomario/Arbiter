package vf.arbiter.client.view.window

import utopia.firmament.context.{ColorContext, TextContext}
import utopia.firmament.image.SingleColorIcon
import utopia.firmament.localization.LocalizedString
import utopia.flow.view.template.eventful.Changing
import utopia.reach.component.factory.ContextualMixed
import utopia.reach.component.template.ReachComponentLike
import utopia.reach.component.wrapper.OpenComponent
import utopia.reach.context.ReachContentWindowContext
import utopia.reach.window.InputWindowFactory

/**
 * Common trait for windows that show input forms
 * @author Mikko Hilpinen
 * @since 2.5.2023, v2.0
 * @tparam A Type of result returned by this window
 * @tparam N Type of context used by this window
 */
trait ArbiterFormWindowFactory[A, N] extends InputWindowFactory[A, N]
{
	// IMPLEMENTED  -----------------------
	
	override protected def fieldCreationContext: ColorContext = ???
	
	override protected def warningPopupContext: ReachContentWindowContext = ???
	
	override protected def closeIcon: SingleColorIcon = ???
	
	override protected def makeFieldNameAndFieldContext(base: ColorContext): (TextContext, TextContext) = ???
	
	override protected def buildLayout(factories: ContextualMixed[ColorContext], content: Vector[OpenComponent[ReachComponentLike, Changing[Boolean]]], context: N): ReachComponentLike = ???
	
	override protected def defaultCloseButtonText: LocalizedString = ???
}
