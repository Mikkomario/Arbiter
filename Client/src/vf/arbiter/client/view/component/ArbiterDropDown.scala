package vf.arbiter.client.view.component

import utopia.firmament.context.TextContext
import utopia.reach.component.factory.FromContextComponentFactoryFactory.Ccff
import utopia.reach.component.hierarchy.ComponentHierarchy

/**
 * Used for constructing drop down lists
 * @author Mikko Hilpinen
 * @since 2.5.2023, v2.0
 */
object ArbiterDropDown /*extends Ccff[TextContext, ArbiterDropDownFactory]
{
	override def withContext(hierarchy: ComponentHierarchy, context: TextContext): ArbiterDropDownFactory =
		new ArbiterDropDownFactory(hierarchy, context)
}*/

/*
class ArbiterDropDownFactory(hierarchy: ComponentHierarchy, override val context: TextContext)
	extends TextContextualFactory[ArbiterDropDownFactory]
{
	// ATTRIBUTES   ---------------------------
	
	private lazy val ddFactory = DropDown.withContext(hierarchy, Context.window.borderless.withTextContext(context))
	
	
	// IMPLEMENTED  ---------------------------
	
	override def self: ArbiterDropDownFactory = this
	
	override def withContext(context: TextContext): ArbiterDropDownFactory =
		new ArbiterDropDownFactory(hierarchy, context)
		
	
	// OTHER    -------------------------------
	
	/**
	 * Creates a new generic drop-down field
	 * @param contentPointer A pointer to the selectable content
	 * @param valuePointer A mutable pointer that contains the selected value (default = new pointer)
	 * @param displayFunction       Display function to use for converting selectable values to text (default = use toString)
	 * @param fieldName             Name displayed on this field (default = empty)
	 * @param prompt                Prompt to display before a value is selected (default = empty)
	 * @param hintPointer           A pointer to the hint displayed under this field (default = always empty)
	 * @param errorMessagePointer   A pointer to the error message displayed on this field (default = always empty)
	 * @param highlightStylePointer A pointer to an additional highlighting style applied to this field (default = always None)
	 * @param leftIconPointer       A pointer to the icon displayed on the left side of this component (default = always none)
	 * @param makeNoOptionsView    An optional function used for constructing the view to display,
	 *                             when no options are available.
	 *                             Accepts 3 parameters:
	 *                             1) Component hierarchy,
	 *                             2) Component creation context, and
	 *                             3) Background color pointer
	 * @param makeAdditionalOption An optional function used for constructing an additional view that is presented
	 *                             under the main selection list. May be used, for example, for providing an "add" option.
	 *                             Accepts 3 parameters:
	 *                             1) Component hierarchy,
	 *                             2) Component creation context, and
	 *                             3) Background color pointer
	 * @param sameItemCheck A function for checking whether two options represent the same instance (optional).
	 *                      Should only be specified when equality function (==) shouldn't be used.
	 * @param makeDisplay A function for constructing new item option fields in the pop-up selection list.
	 *                    Accepts four values:
	 *                    1) A component hierarchy,
	 *                    2) Component creation context,
	 *                    3) Background color pointer
	 *                    4) Item to display initially
	 *                    Returns a properly initialized display
	 * @tparam A Type of selected items
	 * @return A new drop down field
	 */
	def apply[A](contentPointer: Changing[Vector[A]],
	             valuePointer: PointerWithEvents[Option[A]] = PointerWithEvents.empty(),
	             displayFunction: DisplayFunction[Option[A]] = DisplayFunction.rawOption,
	             fieldName: LocalizedString = LocalizedString.empty, prompt: LocalizedString = LocalizedString.empty,
	             hintPointer: Changing[LocalizedString] = LocalizedString.alwaysEmpty,
	             errorMessagePointer: Changing[LocalizedString] = LocalizedString.alwaysEmpty,
	             highlightStylePointer: Changing[Option[ColorRole]] = Fixed(None),
	             leftIconPointer: Changing[SingleColorIcon] = SingleColorIcon.alwaysEmpty,
	             makeNoOptionsView: Option[(ComponentHierarchy, TextContext, Changing[Color]) => ReachComponentLike] = None,
	             makeAdditionalOption: Option[(ComponentHierarchy, TextContext, Changing[Color]) => ReachComponentLike] = None,
	             sameItemCheck: Option[EqualsFunction[A]] = None)
	            (makeDisplay: (ComponentHierarchy, TextContext, A) => ReachComponentLike with Refreshable[A]) =
		ddFactory.apply(contentPointer, valuePointer, icon.drop.down, icon.drop.up, displayFunction,
			Fixed(fieldName), Fixed(prompt), hintPointer, errorMessagePointer, leftIconPointer, Leading,
			margins.aroundVerySmall, makeNoOptionsView, makeAdditionalOption, highlightStylePointer,
			sameItemCheck = sameItemCheck) { (hierarchy, context, _, item) => makeDisplay(hierarchy, context, item) }
	
	/**
	 * Creates a new generic drop-down field
	 * @param contentPointer        A pointer to the selectable content
	 * @param valuePointer          A mutable pointer that contains the selected value (default = new pointer)
	 * @param displayFunction       Display function to use for converting selectable values to text (default = use toString)
	 * @param fieldName             Name displayed on this field (default = empty)
	 * @param prompt                Prompt to display before a value is selected (default = empty)
	 * @param noOptionsText         Text to display when there are no options to select from (default = empty)
	 * @param hintPointer           A pointer to the hint displayed under this field (default = always empty)
	 * @param errorMessagePointer   A pointer to the error message displayed on this field (default = always empty)
	 * @param highlightStylePointer A pointer to an additional highlighting style applied to this field (default = always None)
	 * @param sameItemCheck         A function for checking whether two options represent the same instance (optional).
	 *                              Should only be specified when equality function (==) shouldn't be used.
	 * @tparam A Type of selected items
	 * @return A new drop down field
	 */
	def simple[A](contentPointer: Changing[Vector[A]],
	              valuePointer: PointerWithEvents[Option[A]] = PointerWithEvents.empty(),
	              displayFunction: DisplayFunction[A] = DisplayFunction.raw,
	              fieldName: LocalizedString = LocalizedString.empty, prompt: LocalizedString = LocalizedString.empty,
	              noOptionsText: => LocalizedString = LocalizedString.empty,
	              hintPointer: Changing[LocalizedString] = LocalizedString.alwaysEmpty,
	              errorMessagePointer: Changing[LocalizedString] = LocalizedString.alwaysEmpty,
	              highlightStylePointer: Changing[Option[ColorRole]] = Fixed(None),
	              sameItemCheck: Option[EqualsFunction[A]] = None) =
		apply(contentPointer, valuePointer, DisplayFunction.option(displayFunction), fieldName, prompt, hintPointer,
			errorMessagePointer, highlightStylePointer, SingleColorIcon.alwaysEmpty,
			noOptionsText.notEmpty.map { text => { (hierarchy, context, _) =>
				TextLabel(hierarchy).withContext(context).apply(text, isHint = true)
			} }, sameItemCheck = sameItemCheck) { (hierarchy, context, item) =>
			MutableViewTextLabel(hierarchy).withContext(context).apply(item, displayFunction)
		}
}*/
