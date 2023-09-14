package vf.arbiter.gold.controller.settings

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.generic.model.immutable.ModelDeclaration
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.parse.json.JsonParser
import utopia.flow.parse.string.Regex
import utopia.flow.util.JsonSettingsAccess
import utopia.flow.util.StringExtensions._
import vf.arbiter.core.util.Common.log

/**
 * An interface used for accessing settings concerning gold-related features
 * @author Mikko Hilpinen
 * @since 14.9.2023, v1.4
 */
object ArbiterGoldSettings
{
	// ATTRIBUTES   ----------------------------
	
	implicit val jsonParser: JsonParser = JsonBunny
	
	private val access = new JsonSettingsAccess(
		fileNameRegex = Regex("arbiter") + Regex.any + Regex("settings") + Regex.any,
		schema = ModelDeclaration("apiKey" -> StringType))
		
	private lazy val apiModel = access.required("api").map { _.getModel }
	
	/**
	 * The currency used in metal price API requests. Default = EUR
	 */
	lazy val currencyUnit = access("currency").stringOr("EUR")
	/**
	 * The metal unit used in API requests. Default = AUX (i.e. gold)
	 */
	lazy val metalUnit = access("metal").stringOr("AUX")
		
	
	// COMPUTED -------------------------------
	
	/**
	 * @return The root path / domain of the metal price API
	 */
	def apiRoot = access("api")("root").stringOr("https://api.metalpriceapi.com")
	/**
	 * @return The API-key used for accessing the gold price API.
	 */
	def apiKey = apiModel.flatMap { _("key").tryString }
	/**
	 * @return API version used (default = v1)
	 */
	def apiVersion = access("api")("version").string match {
		case Some(s) => s.startingWith("v")
		case None => "v1"
	}
}
