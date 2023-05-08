package vf.arbiter.client.view.window

import utopia.firmament.context.TextContext
import utopia.firmament.drawing.immutable.BackgroundDrawer
import utopia.firmament.image.SingleColorIcon
import utopia.firmament.localization.LocalizedString
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.util.logging.Logger
import utopia.flow.view.template.eventful.Changing
import utopia.flow.view.template.eventful.FlagLike._
import utopia.paradigm.color.ColorRole
import utopia.paradigm.enumeration.Alignment
import utopia.reach.component.factory.ContextualMixed
import utopia.reach.component.template.ReachComponentLike
import utopia.reach.component.wrapper.OpenComponent
import utopia.reach.container.multi.{Stack, ViewStack}
import utopia.reach.container.wrapper.{ContextualFramingFactory, Framing}
import utopia.reach.context.ReachContentWindowContext
import utopia.reach.window.InputWindowFactory
import vf.arbiter.client.view.ViewSettings._
import vf.arbiter.core.util.Common

import scala.concurrent.ExecutionContext

/**
 * Common trait for windows that show input forms
 * @author Mikko Hilpinen
 * @since 2.5.2023, v2.0
 * @tparam A Type of result returned by this window
 * @tparam N Type of context used by this window
 */
trait ArbiterFormWindowFactory[A, N] extends InputWindowFactory[A, N]
{
	// COMPUTED ---------------------------
	
	protected def formBackground = windowContext.color.dark.primary
	
	
	// IMPLEMENTED  -----------------------
	
	override protected def defaultCloseButtonText: LocalizedString = "Cancel"
	override protected def closeIcon: SingleColorIcon = icon.close
	
	override protected def executionContext: ExecutionContext = Common.executionContext
	override protected def log: Logger = Common.log
	
	override protected def windowContext: ReachContentWindowContext = context.window
	override protected def contentContext: (TextContext, TextContext) = {
		val context = windowContext.textContext.against(formBackground)
		context -> context
	}
	override protected def warningPopupContext: ReachContentWindowContext =
		windowContext.borderless.withAnchorAlignment(Alignment.Left)
			.withWindowBackground(color.scheme.warning.against(formBackground))
	
	override protected def buttonContext(buttonColor: ColorRole, hasIcon: Boolean): TextContext =
		windowContext.textContext/buttonColor
	
	override protected def buildLayout(factories: ContextualMixed[TextContext],
	                                   content: Vector[OpenComponent[ReachComponentLike, Changing[Boolean]]],
	                                   context: N): ReachComponentLike =
	{
		val bg = formBackground
		val insets = margins.aroundSmall
		
		// Places each component (group) into a framing
		def frame[R](framingF: ContextualFramingFactory[_], component: OpenComponent[ReachComponentLike, R]) =
			framingF.withInsets(insets).withCustomDrawer(BackgroundDrawer(bg)).apply(component)
		
		// Uses a different container based on content
		content.oneOrMany match {
			// Case: Only one component => Only frames it
			case Left(component) => frame(factories(Framing), component)
			case Right(components) =>
				// Case: Many components where there are no visibility changes => Stack
				if (content.forall { _.result.isAlwaysTrue })
					factories(Stack).build(Framing) { framingF => components.map { c => frame(framingF, c) } }
				// Case: Many components with visibility changes => View Stack
				else
					factories(ViewStack).build(Framing) { framingF =>
						components.map { c => frame(framingF.next(), c).parentAndResult }
					}
		}
	}
}
