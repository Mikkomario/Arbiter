package vf.arbiter.client.view

import vf.arbiter.core.util.Common._
import utopia.firmament.context.{BaseContext, WindowContext}
import utopia.firmament.localization.{Localizer, NoLocalization}
import utopia.firmament.model.Margins
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.parse.file.FileExtensions._
import utopia.genesis.handling.ActorLoop
import utopia.genesis.handling.mutable.ActorHandler
import utopia.genesis.text.Font
import utopia.genesis.util.Screen
import utopia.paradigm.color.ColorRole.{Gray, Info, Success, Warning}
import utopia.paradigm.color.{ColorScheme, ColorSet}
import utopia.paradigm.generic.ParadigmDataType
import utopia.paradigm.measurement.Ppi
import utopia.paradigm.measurement.DistanceExtensions._
import utopia.paradigm.shape.shape2d.{Insets, Point}
import utopia.reach.context.{ReachContentWindowContext, ReachWindowContext}
import utopia.reach.cursor.{CursorSet, CursorType}

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
	
	implicit val localizer: Localizer = NoLocalization
	implicit val ppi: Ppi = Screen.ppi
	
	val margins = Margins(0.25.cm.toPixels.round.toInt)
	
	/*
	private val cursorsDirectory: Path = "Reach/test-images"
	lazy val cursors: Option[CursorSet] = Image.readFrom(cursorsDirectory/"cursor-arrow.png").toOption.map { arrowImage =>
		val arrowCursor = Cursor(new SingleColorIcon(arrowImage.withSourceResolutionOrigin(Point(7, 4))))
		val handImage = Image.readFrom(cursorsDirectory/"cursor-hand.png").toOption.map { i =>
			new SingleColorIcon(i.withSourceResolutionOrigin(Point(9, 1))) }
		val textImage = Image.readFrom(cursorsDirectory/"cursor-text.png").toOption.map { i =>
			new SingleColorIcon(i.withCenterOrigin) }
		
		CursorSet(Vector(Interactive -> handImage, Text -> textImage)
			.flatMap { case (cursorType, cursor) => cursor.map { cursorType -> Cursor(_) } }
			.toMap[CursorType, Cursor] + (Default -> arrowCursor), arrowCursor)
	}
	 */
	
	val cursors = CursorSet.loadIcons(Map(
		CursorType.Default -> (directory.cursors/"cursor-arrow.png", Point(7, 4)),
		CursorType.Interactive -> (directory.cursors/"cursor-hand.png", Point(9, 1)),
		CursorType.Text -> (directory.cursors/"cursor-text.png", Point(12, 12))
	))
	
	val actorHandler = ActorHandler()
	private val actorLoop = new ActorLoop(actorHandler)
	
	
	// INITIAL CODE ---------------------------
	
	actorLoop.runAsync()
	
	
	// COMPUTED -------------------------------
	
	def font = Fonts
	def color = Colors
	def directory = Directories
	
	
	// NESTED   -------------------------------
	
	object Directories
	{
		val data: Path = "Client/data"
		val images = data/"images"
		val cursors = images/"cursors"
	}
	
	object Context
	{
		val base = BaseContext.apply(actorHandler, font.standard, color.scheme, margins)
		/*
		implicit val window: ReachContentWindowContext =
			ReachWindowContext(WindowContext(actorHandler, screenBorderMargins = Insets.symmetric(margins.small)), ???)*/
	}
	
	object Fonts
	{
		val standard = Font("Arial", 0.5.cm.toPixels.round.toInt)
	}
	
	object Colors
	{
		// ATTRIBUTES   ----------------------
		
		val primary = ColorSet.fromHexes("#08aaf0", "#82d5f7", "#0078ba").get
		val secondary = ColorSet.fromHexes("#f04e08", "#fd875c", "#bc3100").get
		val tertiary = ColorSet.fromHexes("#f008aa", "#f3b9e4", "#cb0088").get
		
		val info = ColorSet.fromHexes("#0836f0", "#717fff", "#0000ca").get
		val success = ColorSet.fromHexes("#08f0c2", "#d7fcf0", "#00d285").get
		
		val scheme = ColorScheme.default ++ ColorScheme.threeTone(primary, secondary, tertiary) ++
			Map(Gray -> gray, Info -> info, Success -> success, Warning -> secondary)
		
		
		// COMPUTED --------------------------
		
		def gray = ColorSet.defaultDarkGray
	}
}
