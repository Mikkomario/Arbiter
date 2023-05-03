package vf.arbiter.client.view

import utopia.firmament.context.{BaseContext, ScrollingContext, WindowContext}
import utopia.firmament.image.SingleColorIconCache
import utopia.firmament.localization.{Localizer, NoLocalization}
import utopia.firmament.model.Margins
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.time.TimeExtensions._
import utopia.genesis.handling.ActorLoop
import utopia.genesis.handling.mutable.ActorHandler
import utopia.genesis.text.Font
import utopia.genesis.util.Screen
import utopia.paradigm.color.ColorRole.{Gray, Info, Success, Warning}
import utopia.paradigm.color.{ColorScheme, ColorSet}
import utopia.paradigm.generic.ParadigmDataType
import utopia.paradigm.measurement.DistanceExtensions._
import utopia.paradigm.measurement.Ppi
import utopia.paradigm.shape.shape2d.{Insets, Point, Size}
import utopia.reach.container.RevalidationStyle.Delayed
import utopia.reach.context.{ReachContentWindowContext, ReachWindowContext}
import utopia.reach.cursor.{CursorSet, CursorType}
import vf.arbiter.core.util.Common._

import java.nio.file.Path

/**
 * Contains commonly used settings for views
 * @author Mikko Hilpinen
 * @since 2.5.2023, v2.0
 */
object ViewSettings
{
	ParadigmDataType.setup()
	
	// ATTRIBUTES   ---------------------------
	
	/**
	 * Implicit language code for hard-coded strings (English)
	 */
	implicit val languageCode: String = "en"
	/**
	 * Implicit localization implementation to use
	 */
	implicit val localizer: Localizer = NoLocalization
	/**
	 * The applicable pixels-per-inch value
	 */
	implicit val ppi: Ppi = Screen.ppi
	
	/**
	 * Margins to use
	 */
	val margins = Margins(0.25.cm.toPixels.round.toInt)
	
	/**
	 * Cursors to use
	 */
	val cursors = CursorSet.loadIcons(Map(
		CursorType.Default -> (directory.cursors/"cursor-arrow.png", Point(7, 4)),
		CursorType.Interactive -> (directory.cursors/"cursor-hand.png", Point(9, 1)),
		CursorType.Text -> (directory.cursors/"cursor-text.png", Point(12, 12))
	)).logToOption
	
	/**
	 * Actor handler that delivers action events
	 */
	val actorHandler = ActorHandler()
	private val actorLoop = new ActorLoop(actorHandler)
	
	/**
	 * Settings for constructing scroll views
	 */
	implicit val scrollContext: ScrollingContext = ScrollingContext.withDarkRoundedBar(actorHandler)
	
	
	// INITIAL CODE ---------------------------
	
	actorLoop.runAsync()
	
	
	// COMPUTED -------------------------------
	
	/**
	 * @return Access point to standard context instances
	 */
	def context = Context
	/**
	 * @return Access point to icons
	 */
	def icon = Icons
	/**
	 * @return Access point to fonts
	 */
	def font = Fonts
	/**
	 * @return Access point to colors
	 */
	def color = Colors
	/**
	 * @return Access point to directories
	 */
	def directory = Directories
	
	
	// NESTED   -------------------------------
	
	object Directories
	{
		/**
		 * The directory that contains all program data
		 */
		val data: Path = "Client/data"
		/**
		 * The directory that contains image files
		 */
		val images = data/"images"
		/**
		 * The directory that contains cursor image files
		 */
		val cursors = images/"cursors"
	}
	
	object Context
	{
		/**
		 * The standard base context to use
		 */
		val base = BaseContext.apply(actorHandler, font.standard, color.scheme, margins)
		/**
		 * The window-creation context to use
		 */
		implicit val window: ReachContentWindowContext =
			ReachWindowContext(WindowContext(actorHandler, screenBorderMargins = Insets.symmetric(margins.small)),
				color.primary, cursors, Delayed.by(0.1.seconds, 0.25.seconds))
				.withContentContext(base)
	}
	
	object Fonts
	{
		/**
		 * The standard font to use
		 */
		val standard = Font("Arial", 0.5.cm.toPixels.round.toInt)
	}
	
	object Colors
	{
		// ATTRIBUTES   ----------------------
		
		/**
		 * Primary colors
		 */
		val primary = ColorSet.fromHexes("#08aaf0", "#82d5f7", "#0078ba").get
		/**
		 * Secondary colors
		 */
		val secondary = ColorSet.fromHexes("#f04e08", "#fd875c", "#bc3100").get
		/**
		 * Tertiary colors
		 */
		val tertiary = ColorSet.fromHexes("#f008aa", "#f3b9e4", "#cb0088").get
		
		/**
		 * Colors for info elements
		 */
		val info = ColorSet.fromHexes("#0836f0", "#717fff", "#0000ca").get
		/**
		 * Colors to represent success
		 */
		val success = ColorSet.fromHexes("#08f0c2", "#d7fcf0", "#00d285").get
		
		/**
		 * The color scheme to use
		 */
		val scheme = ColorScheme.default ++ ColorScheme.threeTone(primary, secondary, tertiary) ++
			Map(Gray -> gray, Info -> info, Success -> success, Warning -> secondary)
		
		
		// COMPUTED --------------------------
		
		/**
		 * @return Gray colors to use
		 */
		def gray = ColorSet.defaultDarkGray
	}
	
	object Icons
	{
		// ATTRIBUTES   ---------------------
		
		private val cache = new SingleColorIconCache(directory.images/"icons", Some(Size.square(1.cm.toPixels)))
		
		
		// COMPUTED ------------------------
		
		/**
		 * @return Access to drop-down icons
		 */
		def drop = DdIcons
		
		/**
		 * @return An icon for closing action (X)
		 */
		def close = cache("close")
		
		
		// NESTED   -----------------------
		
		object DdIcons
		{
			/**
			 * @return Drop down icon
			 */
			def down = cache("drop-down")
			/**
			 * @return Drop up icon
			 */
			def up = cache("drop-up")
		}
	}
}
